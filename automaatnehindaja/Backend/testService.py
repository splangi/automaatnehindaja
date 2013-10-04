import MySQLdb as mdb
from time import sleep
from subprocess import Popen, STDOUT, PIPE

#Function for connecting the MySQL database
#Returns cursor to access database
def connectToDatabase():
    cnx = mdb.connect(host='localhost',user='root',passwd='t6urott',db='automaatnehindaja')
    cnx.autocommit(True)
    cursor = cnx.cursor()
    return cursor


#Function for checking if there is unvaluated attempts in the database
#Returns data about the attempt
def checkForAttempts(cursor):
    '''
    queryForAttempts = ("SELECT * FROM attempt WHERE result='Kontrollimata' LIMIT 0, 1")
    cursor.execute(queryForAttempts)
    if (cursor.rowcount==1):
        for (line) in cursor:
            taskId = line[0]
            username = line[1]
            task = line[2]
            time = line[3]
            result = line[4]
            source_code = line[5]
            language = line[6]
            #Set result to 'Kontrollimisel'
            queryForResultUpdate = ("UPDATE attempt SET result='Kontrollimisel' WHERE id=%s")
            cursor.execute(queryForResultUpdate,(taskId))
            return (taskId, username, task, time, result, source_code, language)
    else:
        sleep(5)
        tester()
    '''
    queryForAttempts = ("SELECT * FROM attempt WHERE result='Kontrollimata' LIMIT 0, 1")
    cursor.execute(queryForAttempts)
    while (cursor.rowcount==0):
        #print ('hibernate')
        sleep(5)
        queryForAttempts = ("SELECT * FROM attempt WHERE result='Kontrollimata' LIMIT 0, 1")
        cursor.execute(queryForAttempts)
    for (line) in cursor:
        taskId = line[0]
        username = line[1]
        task = line[2]
        time = line[3]
        result = line[4]
        source_code = line[5]
        language = line[6]
        #Set result to 'Kontrollimisel'
        queryForResultUpdate = ("UPDATE attempt SET result='Kontrollimisel' WHERE id=%s")
        cursor.execute(queryForResultUpdate,(taskId))
        return (taskId, username, task, time, result, source_code, language)


#Function for getting input and expected output for the task
#Returns input and output
def getTasksInputAndOutput(cursor, task):
    queryForIO = ("SELECT input, output FROM tasks WHERE id=%s")
    cursor.execute(queryForIO, (task))

    for(line) in cursor:
        taskInput = line[0]
        taskOutput = line[1]
        return(taskInput, taskOutput)


#Function for writing source code to file delivered by student
def writeSourcecodeToFile(source_code):
    file = open('temp.py','w')
    file.write("".join(source_code))
    

#Function for running students source code
#Returns applications output
def runStudentsAttempt(taskInput):
    taskInput2 = taskInput.replace(',','"\n"')
    taskInput2 = '"'+taskInput2+'"'
    connectionToAttempt = Popen(['python','lahendus.py'],stdout=PIPE, stdin=PIPE, stderr=STDOUT)
    applicationOutput = connectionToAttempt.communicate (taskInput2.encode('utf-8'))[0]
    return applicationOutput

#Checks if the expected output occurs in the application output
#Returns boolean
def checkOutputCorrectness(taskOutput, applicationOutput):
    split = taskOutput.split(',')
    for line in split:
        if line not in applicationOutput:
            return False
    return True


#Updates the database with new result. 'OK' if output was right and 'Vale tulemus' if wrong
def updateDatabase(cursor, taskOutput, applicationOutput, taskId):
    if(checkOutputCorrectness(taskOutput, applicationOutput)):        
        #Check if the result is still 'Kontrollimisel'
        queryForResultCheck = ("SELECT result FROM attempt WHERE id=%s")
        cursor.execute(queryForResultCheck,(taskId))
        for line in cursor:
            if line[0]=='Kontrollimisel':
                #Set result to 'OK'
                queryForResultUpdate = ("UPDATE attempt SET result='OK' WHERE id=%s")
                cursor.execute(queryForResultUpdate,(taskId))
                tester()
            else:
                tester()
    else:
        #Set result to 'Vale tulemus'
        queryForResultUpdate = ("UPDATE attempt SET result='Vale tulemus' WHERE id=%s")
        cursor.execute(queryForResultUpdate,(taskId))
        tester()
        
    
def tester():
    cursor = connectToDatabase()        
    taskId, username, task, time, result, source_code, language = checkForAttempts(cursor)
    taskInput, taskOutput = getTasksInputAndOutput(cursor, task)
    writeSourcecodeToFile(source_code)
    applicationOutput = runStudentsAttempt(taskInput)
    updateDatabase(cursor, taskOutput, applicationOutput, taskId)

tester()

#TODO
# * Lahendada probleem while tsukliga
# * sql commandid koondada
# * timeout kontroll
# * kompileerimise vea kontroll