import random
import socket, select
from time import gmtime, strftime
from random import randint
from picamera import PiCamera
from time import sleep

camera = PiCamera()
curImageLoc = '/home/pi/Desktop/image.png' \
              '' \
              ''

#Find the specified object on camera, and give visual feedback

HOST2 = '192.168.50.45'
PORT2 = 6666
sock2 = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_address = (HOST2, PORT2)
sock2.connect(server_address)

def get_rec_from_mac(theName,image):
    # open image
    myfile = open(image, 'rb')
    bytes = myfile.read()

    # send image name to server
    sock2.sendall("NAME %s" % theName)
    answer = sock2.recv(4096)

    print 'answer = %s' % answer

    # send image to server
    sock2.sendall(bytes)
    # check what server send
    answer = sock2.recv(4096)
    print 'answer = %s' % answer
    words = answer.split()
    x0 = int(words[0])
    y0 = int(words[1])
    x1 = int(words[2])
    y1 = int(words[3])
    w = int(words[4])
    h = int(words[5])
    return (x0,y0,x1,y1,w,h)
    myfile.close()

def find(sock, name):
    time = 0
    reportInterval = 5
    hoDegreeRange = 75
    while True:
        if (time % reportInterval == 0):
            camera.capture(curImageLoc)
            sleep(0.1)
            (x0, y0, x1, y1, w, h) = get_rec_from_mac(name, curImageLoc)
            if x0 != 0 or y0 != 0 or w != 0 or h != 0:
                sock.sendall("Found" + "\n")
                hoCenter = (x0+x1)/2
                relativePos = ((hoCenter / w) - 0.5) * hoDegreeRange
                if relativePos > 0:
                    sock.sendall(str(relativePos) + "degrees to the right")
                elif relativePos < 0:
                    sock.sendall(str(-relativePos) + "degrees to the left")
                else:
                    sock.sendall("right at your front")
                sock.sendall("starting ranging")
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


