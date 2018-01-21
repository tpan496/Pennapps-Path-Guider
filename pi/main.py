import random
import socket, select
from time import gmtime, strftime
from random import randint
from recognition import *
from picamera import PiCamera
from time import sleep

camera = PiCamera()
curImageLoc = '/home/pi/Desktop/image.jpg'

#Find the specified object on camera, and give visual feedback

def get_rec_from_mac(name,image):
    HOST = '192.168.50.45'
    PORT = 6666

    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_address = (HOST, PORT)
    sock.connect(server_address)

    try:

        # open image
        myfile = open(image, 'rb')
        bytes = myfile.read()
        name = "aa"

        # send image name to server
        sock.sendall("NAME %s" % name)
        answer = sock.recv(4096)

        print 'answer = %s' % answer

        # send image to server
        sock.sendall(bytes)
        # check what server send
        answer = sock.recv(4096)
        words = answer.split()
        x0 = int(tmp[0])
        y0 = int(tmp[1])
        x1 = int(tmp[2])
        y1 = int(tmp[3])
        w = int(tmp[4])
        h = int(tmp[5])
        return (x0,y0,x1,y1,w,h)
        myfile.close()


    finally:
        sock.close()

def find(sock, name):
    time = 0
    reportInterval = 5
    hoDegreeRange = 75
    while True:
        if (time % reportInterval == 0):
            camera.capture(curImageLoc)
            (x0, y0, x1, y1, w, h) = get_rec_from_mac(imgPrefix + name, curImageLoc)
            if x0 != 0 or y0 != 0 or w != 0 or h != 0:
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
        time += 1


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
        if data.startswith("FIND"):
            tmp = txt.split()
            name = tmp[1]
            sock.sendall("Start rotating.\n")
            find(sock,name)


sock.close()


