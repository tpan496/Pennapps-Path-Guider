import random
import socket, select
from time import gmtime, strftime
from random import randint
from recgnition import *
from picamera import PiCamera
from time import sleep

camera = PiCamera()
imgPrefix = "/home/pi/Desktop/images/image_"
curImageLoc = '/home/pi/Desktop/image.jpg'

#Find the specified object on camera, and give visual feedback
def find(sock, name):
    time = 0
    reportInterval = 5
    hoDegreeRange = 75
    while True:
        camera.capture(curImageLoc)
        (x0,y0,x1,y1,w,h) = recognition(imgPrefix+name,curImageLoc)
        time += 1
        if (time % reportInterval == 0):
            if x != 0 or y != 0 or w != 0 or h != 0:
                sock.sendall("Found" + "\n")
                hoCenter = (x0+x1)/2
                relativePos = ((hoCenter / w) - 0.5) * hoDegreeRange
                if relativePos > 0:
                    sock.sendall(relativePos + "degrees to the right")
                elif relativePos < 0:
                    sock.sendall(-relativePos + "degrees to the left")
                else:
                    sock.sendall("right at your front")
                sock.sendall("starting ranging")
                ##开始bbb测距模式
                break



HOST = '192.168.50.64'
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
        print data
        if data.startswith("DEF"):
            tmp = txt.split()
            name = tmp[1]
            sock.sendall("INSTR Hold your camera towards the item, and say ready when ready.\n")
        elif data.startswith("FIND"):
            tmp = txt.split()
            name = tmp[1]
            sock.sendall("INSTR Start rotating.\n")
            find(sock,name)
        elif data.startswith("READY") :
            camera.capture(imgPrefix+name)
            sock.sendall("INSTR Your image is defined.\n")

sock.close()


