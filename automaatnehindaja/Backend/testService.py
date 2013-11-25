import MySQLdb as mdb
from time import sleep
from subprocess import Popen, STDOUT, PIPE
import datetime, time
import os
import smtplib
import traceback


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
    queryForAttempts = ("SELECT attempt.id,attempt.username, attempt.task, attempt.time, attempt.result, attempt.source_code, attempt.language, tasks.name, tasks.coursename FROM attempt, tasks WHERE attempt.result='Kontrollimata' AND attempt.task=tasks.id LIMIT 0, 1")
    cursor.execute(queryForAttempts)
    while (cursor.rowcount==0):
        for i in range(cwaittime):
            sleep(1)
        queryForAttempts = ("SELECT attempt.id,attempt.username, attempt.task, attempt.time, attempt.result, attempt.source_code, attempt.language, tasks.name, tasks.coursename FROM attempt, tasks WHERE attempt.result='Kontrollimata' AND attempt.task=tasks.id LIMIT 0, 1")
        cursor.execute(queryForAttempts)
    for (line) in cursor:
        attemptId = line[0]
        username = line[1]
        task = line[2]
        time = line[3]
        result = line[4]
        source_code = line[5]
        language = line[6]
        taskName = line[7]
        courseName = line[8]
        queryForResultUpdate = ("UPDATE attempt SET result='Kontrollimisel' WHERE id=%s")
        cursor.execute(queryForResultUpdate,(attemptId))
        logger(cursor, 'INFO', 'Start checking task. Attempt Id: ' + str(attemptId) + '. Username: ' + str(username) + '. Task Id: ' + str(task) +'.')
        return (attemptId, username, task, time, result, source_code, language, taskName, courseName)


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
def runStudentsAttempt(taskInputArray, taskOutputArray, language, cursor, attemptId, username, task, source_code, taskName, courseName):
    resultRight = True
    databaseUpdated = False

    queryForAttemptOutput = ("DELETE FROM attempt_output where attempt_id=%s")
    cursor.execute(queryForAttemptOutput,(attemptId))
    
    for i in range(len(taskInputArray)):
        inputString = ''
        for j in range(len(taskInputArray[i])):
            inputString += taskInputArray[i][j]
            if (j != len(taskInputArray[i])-1):
                inputString += '\n'
        startTime = datetime.datetime.now()
        if (language == 'Python 3'):
            stri = 'timeout ' + str(int(ctimeout)+1) + ' python3 ' + clocation +'/temp.py'
            connectionToAttempt = Popen(['/bin/su', '-', cusername, '-c', stri],stdout=PIPE, stdin=PIPE, stderr=STDOUT)

            #connectionToAttempt = Popen(['timeout',ctimeout,'python3','temp.py'],stdout=PIPE, stdin=PIPE, stderr=STDOUT)
        elif (language == 'Python 2'):
            stri = 'timeout ' + str(int(ctimeout)+1) + ' python ' + clocation +'/temp.py'
            connectionToAttempt = Popen(['/bin/su', '-', cusername, '-c', stri],stdout=PIPE, stdin=PIPE, stderr=STDOUT)
        
            #connectionToAttempt = Popen(['timeout',ctimeout,'python','temp.py'],stdout=PIPE, stdin=PIPE, stderr=STDOUT)
        applicationOutput = connectionToAttempt.communicate (inputString.encode('utf-8'))[0]
        endTime = datetime.datetime.now()
        deltaTime = endTime - startTime

        if ('Permission denied' in applicationOutput):
            sendEmail(courseName, taskName, username, applicationOutput, source_code, cursor)
        
        
        if (len(applicationOutput)>int(coutputlen)):
            applicationOutput=''
            if (not databaseUpdated):
                updateDatabase(cursor, attemptId, 'Timeout')
                resultRight=False
                databaseUpdated = True
            return

        queryForAttemptOutput = ("INSERT INTO attempt_output (attempt_id, seq, output) VALUES (%s, %s, %s)")
        cursor.execute(queryForAttemptOutput,(attemptId, i, applicationOutput))
        if (deltaTime.seconds >= int(ctimeout)):
            if (not databaseUpdated):
                updateDatabase(cursor, attemptId, 'Timeout')
                resultRight=False
                databaseUpdated = True
            return
        elif (('Error' in applicationOutput) & ('Traceback' in applicationOutput)):
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
    logger(cursor, 'INFO','Finished cheking task. Attempt Id: ' + str(attemptId) + '. Result: ' + result + '.')

def sendEmail(courseName, taskName, username, applicationOutput, source_code, cursor):    
    m_user = 'automaatkontroll@gmail.com'
    m_pwd = 'k1rven2gu'
    m_from ='automaatkontroll@gmail.com'
    m_to = ['blackn' + 'o@gmail.com']
    '''
    queryForResponsibleEmails = ("SELECT users_courses.username FROM users_courses INNER JOIN users_roles ON users_roles.username = users_courses.username WHERE users_courses.coursename = %s AND users_roles.rolename = 'responsible'")
    cursor.execute(queryForResponsibleEmails, (courseName))
    for (line) in cursor:
        m_to.append(line[0])
    '''
    m_subject = 'Hoiatus'
    m_text = 'Kursus: ' + courseName + '\n\nUlesanne: ' + taskName + '\n\nKasutaja: ' + username + '\n\nValjund: \n' + applicationOutput +  '\n\nLahtekood: \n' + source_code
    m_message = """\From: %s\nTo: %s\nSubject: %s\n\n%s """ % (m_from, ', '.join(m_to), m_subject, m_text)

    m_server = smtplib.SMTP('smtp.gmail.com', 587)
    m_server.ehlo()
    m_server.starttls()
    m_server.login(m_user, m_pwd)
    m_server.sendmail(m_from, m_to, m_message)

def logger(cursor, level, message):
    timeS = datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    queryToUpdateLogs = ("INSERT INTO LOGS (DATED, LOGGER, LEVEL, MESSAGE) VALUES (%s, %s, %s, %s)")
    cursor.execute(queryToUpdateLogs,(timeS, 'automaatnehindaja.testService',level, message))

while (True):
    try:
        cursor = connectToDatabase()
        attemptId, username, task, time, result, source_code, language, taskName, courseName = checkForAttempts(cursor)
        taskInputArray = getTasksInput(cursor, task)
        taskOutputArray = getTasksExOutput(cursor, task)
        writeSourcecodeToFile(source_code)
        runStudentsAttempt(taskInputArray, taskOutputArray, language, cursor, attemptId, username, task, source_code, taskName, courseName)
        sleep(1)
    except Exception, ex:
        logger(cursor, 'ERROR',traceback.format_exc())
        
        
