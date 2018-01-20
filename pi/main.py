import random
import socket, select
from time import gmtime, strftime
from random import randint
from recgnition import *
#from picamera import PiCamera
from time import sleep

#camera = PiCamera()
imgPrefix = "/home/pi/Desktop/images/image_"
curImageLoc = '/home/pi/Desktop/image.jpg'

#Find the specified object on camera, and give visual feedback
def find(sock, name):
    time = 0
    interval = 0.1
    reportInterval = 10
    while True:
        #camera.capture(curImageLoc)
        (x,y,w,h) = recgnition(imgPrefix+name,curImageLoc)
        sleep(interval)
        time += 1
        if (time % reportInterval == 0):
            sock.sendall("instr hahahahahaha")







HOST = '192.168.50.64/24'
PORT = 6666
#setup sockets
serversock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
serversock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
serversock.bind((HOST, PORT))
serversock.listen(10)

while True:
    sock, client_address = serversock.accept()
    data = sock.recv(4096).decode('UTF-8')
    txt = str(data)

    name = ""
    size = 0
    if data:
        if data.startswith("DEF"):
            tmp = txt.split()
            name = tmp[1]
            size = int(tmp[2])
            print 'got size'
            sock.sendall("GOTDEF")
        elif data.startswith("FIND"):
            tmp = txt.split()
            name = tmp[1]
            sock.sendall("GOTFIND")
            find(sock,name)
        else :
            myfile = open(imgPrefix+name, 'wb')
            myfile.write(data)
            data = sock.recv(40960000)
            if not data:
                myfile.close()
            else:
                myfile.write(data)
                myfile.close()
            sock.sendall("GOTIMAGE")

sock.close()


