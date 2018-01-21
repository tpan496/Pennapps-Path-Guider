import random
import struct
import io
import socket, select
import time
from random import randint
from picamera import PiCamera
from time import sleep

camera = PiCamera()
camera.resolution = (640, 480)
curImageLoc = '/home/pi/Desktop/image.png'
#Find the specified object on camera, and give visual feedback

def get_rec_from_mac(theName):
    client_socket = socket.socket()
    client_socket.connect(('192.168.50.45', 6666))

    # Make a file-like object out of the connection
    connection = client_socket.makefile('wb')
    try:
        time.sleep(2)
        stream = io.BytesIO()
        for foo in camera.capture_continuous(stream, 'jpeg'):
            print "SARTTSDOI!!!!"
            connection.write(struct.pack('<L', stream.tell()))
            connection.flush()
            stream.seek(0)
            connection.write(stream.read())
            break;
        connection.write(struct.pack('<L', 0))
    finally:
        print "1"
        client_socket.sendall(theName)
        print "2"
        sentence = client_socket.recv(4096)
        tmp = sentence.split()
        connection.close()
        client_socket.close()
        print "OSIJDOFIJSDFOIJSDFOISJDFOSIDJ"
        print sentence
        return (float(tmp[0]),float(tmp[1]),float(tmp[2]),float(tmp[3]),float(tmp[4]),float(tmp[5]))

def find(sock, name):
    time = 0
    reportInterval = 5
    hoDegreeRange = 75
    while True:
        print "ITERATION"
        if (time % reportInterval == 0):
            (x0, y0, x1, y1, w, h) = get_rec_from_mac(name)
            sleep(0.1)
            if x0 != 0 or y0 != 0 or w != 0 or h != 0:
                print x0
                print y0
                print x1
                print y1
                sock.sendall("Found" + "\n")
                sleep(1)
                hoCenter = (x0+x1)/2
                relativePos = ((hoCenter / w) - 0.5) * hoDegreeRange
                if relativePos > 0:
                    sock.sendall(str(relativePos) + "degrees to the right\n")
                elif relativePos < 0:
                    sock.sendall(str(-relativePos) + "degrees to the left\n")
                else:
                    sock.sendall("right in front of you\n")
                print relativePos
                sleep(2)
                sock.sendall("starting ranging\n")
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
    data = sock.recv(4096)
    txt = str(data)

    name = ""
    size = 0
    if data:
        if data.startswith("FIND"):
            tmp = txt.split()
            name = tmp[1]
            sock.sendall("Start rotating.\n")
            find(sock,name)


