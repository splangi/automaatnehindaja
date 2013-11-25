# Detect_plagiarism
# Vineet Nair
# 01-30-2012
# https://github.com/nairv/detect_plagiarism

import sys, re, os, time, datetime
import MySQLdb as mdb


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


def connectToDatabase():
    cnx = mdb.connect(host=chost,user=cuser,passwd=cpasswd,db=cdb)
    cnx.autocommit(True)
    cursor = cnx.cursor()
    return cursor

#Function for checking if there is unvaluated attempts in the database
#Returns data about the attempt
def checkForAttempts(cursor):
    queryForAttempts = ("SELECT attempt.id, attempt.task, attempt.source_code FROM attempt WHERE attempt.plagirismcheck='UNCHECKED' LIMIT 0, 1")
    cursor.execute(queryForAttempts)
    while (cursor.rowcount==0):
        for i in range(cwaittime):
            time.sleep(1)
        queryForAttempts = ("SELECT attempt.id, attempt.task, attempt.source_code FROM attempt WHERE attempt.plagirismcheck='UNCHECKED' LIMIT 0, 1")
        cursor.execute(queryForAttempts)
    for (line) in cursor:
        attemptId = line[0]
        task = line[1]
        source_code = line[2]
        deletePreviousRows = ("DELETE FROM plagiarism WHERE attempt1_id = %s OR attempt2_id = %s")
        cursor.execute(deletePreviousRows,(attemptId, attemptId))
        
        pullOtherAttempts(task, attemptId, source_code, cursor)
        
        updateTaskStatus = ("UPDATE attempt SET plagirismcheck='DONE' where id=%s")
        cursor.execute(updateTaskStatus, (attemptId))


def pullOtherAttempts(taskId, attemptId, source_code, cursor):
    queryForAttempts = ("SELECT attempt.id, attempt.task, attempt.source_code FROM attempt where task=%s and id!=%s LIMIT 0, 100")
    cursor.execute(queryForAttempts,(taskId, attemptId))
    for (line) in cursor:
        compareSourceCode(source_code, line[2], attemptId, line[0], taskId, cursor)

def compareSourceCode(source_code1, source_code2, attemptId1, attemptId2, taskId, cursor):
    linex = source_code1
    liney = source_code2
    tokenizer = re.compile('[a-zA-Z0-9]+', re.IGNORECASE)

    tokenx= tokenizer.findall(linex)
    tokenx.insert(0 , 0)
    tokeny = tokenizer.findall(liney)
    tokeny.insert(0 , 1)

    cc = [[0 for col in range(len(tokeny)+1)] for row in range(len(tokenx)+1)]
    ll = [[0 for col in range(len(tokeny)+1)] for row in range(len(tokenx)+1)]

    for x in range(1 , len(tokenx)):
        for y in range(1 , len(tokeny)):
            if tokenx[x] == tokeny[y]:
                cc[x][y] = cc[x-1][y-1] + len(tokenx[x])**2
                ll[x][y] = ll[x-1][y-1] + 1   
            else:
                if cc[x-1][y] >= cc[x][y-1]:
                    ll[x][y] = ll[x-1][y]
                    cc[x][y] = cc[x-1][y]
                else:
                    ll[x][y] = ll[x][y-1]
                    cc[x][y] = cc[x][y-1]

    p = len(tokenx)-1
    q = len(tokeny)-1
    '''
    print (attemptId1, attemptId2)
    print "The total no of words in the original text is " , p , "."
    print "The total no of words in the test text is " , q , "."
    print "The length of the common subsequence is " , max(max(ll)) , "."
    print "The plagiarism value calculated is " , max(max(cc)) , "."
    '''
    
    rating = ((max(max(ll))*200)/(p+q))
    if (rating > 24):
        updateDatabase(taskId, attemptId1, attemptId2, rating, cursor)

def updateDatabase(taskId, attemptId1, attemptId2, rating, cursor):
    timeS = datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    updateDatabase = ("INSERT INTO plagiarism (task_id, attempt1_id, attempt2_id, rating, time) VALUES (%s, %s, %s, %s, %s)")
    cursor.execute(updateDatabase,(taskId, attemptId1, attemptId2, rating, timeS))
    
while (True):
    checkForAttempts(connectToDatabase())
