import MySQLdb as mdb
from time import sleep
from subprocess import Popen, STDOUT, PIPE
import datetime
import os


#Database
chost = ''
cuser = ''
cpasswd = ''
cdb = ''

#Application
ctimeout = ''
coutputlen = ''
cwaittime = ''
cusername = ''
clocation = os.getcwd()


#Function for reading configuration
if (True):
    f = open(clocation+'/config')
    for line in f:
        if (line.startswith('host')):
            chost = line.split('=')[1].strip()
        elif (line.startswith('dbuser')):
            cuser = line.split('=')[1].strip()
        elif (line.startswith('passwd')):
            cpasswd = line.split('=')[1].strip()
        elif (line.startswith('host')):
            chost = line.split('=')[1].strip()
        elif (line.startswith('database')):
            cdb = line.split('=')[1].strip()
        elif (line.startswith('timeout')):
            ctimeout = line.split('=')[1].strip()
        elif (line.startswith('output_length')):
            coutputlen = int(line.split('=')[1].strip())
        elif (line.startswith('wait_time')):
            cwaittime = int(line.split('=')[1].strip())
        elif (line.startswith('username')):
            cusername = line.split('=')[1].strip()
    
    

#Function for connecting the MySQL database
#Returns cursor to access database
def connectToDatabase():
    cnx = mdb.connect(host=chost,user=cuser,passwd=cpasswd,db=cdb)
    cnx.autocommit(True)
    cursor = cnx.cursor()
    return cursor


#Function for checking if there is unvaluated attempts in the database
#Returns data about the attempt
def checkForAttempts(cursor):
    queryForAttempts = ("SELECT * FROM attempt WHERE result='Kontrollimata' LIMIT 0, 1")
    cursor.execute(queryForAttempts)
    while (cursor.rowcount==0):
        for i in range(cwaittime):
            sleep(1)
        queryForAttempts = ("SELECT * FROM attempt WHERE result='Kontrollimata' LIMIT 0, 1")
        cursor.execute(queryForAttempts)
    for (line) in cursor:
        attemptId = line[0]
        username = line[1]
        task = line[2]
        time = line[3]
        result = line[4]
        source_code = line[5]
        language = line[6]
        queryForResultUpdate = ("UPDATE attempt SET result='Kontrollimisel' WHERE id=%s")
        cursor.execute(queryForResultUpdate,(attemptId))
        return (attemptId, username, task, time, result, source_code, language)


#Function for getting input for the task
#Returns input and output
def getTasksInput(cursor, task):
    queryForIO = ("SELECT outer_seq, inner_seq, input FROM tasks_input WHERE task_id=%s ORDER BY outer_seq, inner_seq ASC")
    cursor.execute(queryForIO, (task))
    outerArray = []
    innerArray = []
    outerCounter = 0
    for (line) in cursor:
        outer_seq = line[0]
        inner_seq = line[1]
        taskInput = line[2]
        if (outer_seq != outerCounter) :
            outerCounter = outer_seq
            outerArray.append(innerArray)
            innerArray = []
            innerArray.append(taskInput)
        else:
            innerArray.append(taskInput)
    outerArray.append(innerArray)
    return outerArray


#Function for getting expected output for the task
#Returns expected output
def getTasksExOutput(cursor, task):
    queryForIO = ("SELECT outer_seq, inner_seq, output FROM tasks_output WHERE task_id=%s ORDER BY outer_seq, inner_seq ASC")
    cursor.execute(queryForIO, (task))
    outerArray = []
    innerArray = []
    outerCounter = 0
    for (line) in cursor:
        outer_seq = line[0]
        inner_seq = line[1]
        taskInput = line[2]
        if (outer_seq != outerCounter):
            outerCounter = outer_seq
            outerArray.append(innerArray)
            innerArray = []
            innerArray.append(taskInput)
        else:
            innerArray.append(taskInput)
    outerArray.append(innerArray)
    return outerArray


#Function for writing source code to file delivered by student
def writeSourcecodeToFile(source_code):
    file = open('temp.py','w')
    file.write("".join(source_code))
    file.close()
    

#Function for running students source code
#Function also checks for timeout, compile errors and checks if the answer is correct
def runStudentsAttempt(taskInputArray, taskOutputArray, language, cursor, attemptId):
    resultRight = True
    databaseUpdated = False
    for i in range(len(taskInputArray)):
        inputString = ''
        for j in range(len(taskInputArray[i])):
            inputString += taskInputArray[i][j]
            if (j != len(taskInputArray[i])-1):
                inputString += '\n'
        startTime = datetime.datetime.now()
        if (language == 'Python 3'):
            stri = 'timeout ' + ctimeout + ' python3 ' + clocation + '/temp.py'
            connectionToAttempt = Popen(['/bin/su', '-', cusername, '-c', stri],stdout=PIPE, stdin=PIPE, stderr=STDOUT)

            #connectionToAttempt = Popen(['timeout',ctimeout,'python3','temp.py'],stdout=PIPE, stdin=PIPE, stderr=STDOUT)
        elif (language == 'Python 2'):
            stri = 'timeout ' + ctimeout + ' python ' + clocation +'/temp.py'
            connectionToAttempt = Popen(['/bin/su', '-', cusername, '-c', stri],stdout=PIPE, stdin=PIPE, stderr=STDOUT)
        
            #connectionToAttempt = Popen(['timeout',ctimeout,'python','temp.py'],stdout=PIPE, stdin=PIPE, stderr=STDOUT)
        applicationOutput = connectionToAttempt.communicate (inputString.encode('utf-8'))[0]
        endTime = datetime.datetime.now()
        deltaTime = endTime - startTime
        if (len(applicationOutput)>coutputlen):
            applicationOutput=''
            if (not databaseUpdated):
                updateDatabase(cursor, attemptId, 'Timeout')
                resultRight=False
                databaseUpdated = True
            return
        queryForAttemptOutput = ("DELETE FROM attempt_output where attempt_id=%s AND seq=%s")
        cursor.execute(queryForAttemptOutput,(attemptId, i))
        queryForAttemptOutput = ("INSERT INTO attempt_output (attempt_id, seq, output) VALUES (%s, %s, %s)")
        cursor.execute(queryForAttemptOutput,(attemptId, i, applicationOutput))
        
        if (deltaTime.seconds >= ctimeout):
            if (not databaseUpdated):
                updateDatabase(cursor, attemptId, 'Timeout')
                resultRight=False
                databaseUpdated = True
            return
        elif (('Error' in applicationOutput) & ('Traceback' in applicationOutput) & (clocation in applicationOutput)):
            if (not databaseUpdated):
                updateDatabase(cursor, attemptId, 'Kompileerimise viga')
                resultRight=False
                databaseUpdated = True
            return
        else:
            aOutput = applicationOutput.split('\n')
            for k in range(len(taskOutputArray[i])):
                if (aOutput[k].rstrip() != taskOutputArray[i][k].rstrip()):
                    if (not databaseUpdated):
                        updateDatabase(cursor, attemptId, 'Vale tulemus')
                        resultRight=False
                        databaseUpdated = True
                    return
    if (resultRight):
        if (not databaseUpdated):
            updateDatabase(cursor, attemptId, 'OK')


#Updates the database with new result. 'OK' if output was right and 'Vale tulemus' if wrong
def updateDatabase(cursor, attempId, result):
    queryForResultUpdate = ("UPDATE attempt SET result=%s WHERE id=%s AND result='Kontrollimisel'")
    cursor.execute(queryForResultUpdate,(result, attemptId))



while (True):
    cursor = connectToDatabase()
    attemptId, username, task, time, result, source_code, language = checkForAttempts(cursor)
    taskInputArray = getTasksInput(cursor, task)
    taskOutputArray = getTasksExOutput(cursor, task)
    writeSourcecodeToFile(source_code)
    runStudentsAttempt(taskInputArray, taskOutputArray, language, cursor, attemptId)
