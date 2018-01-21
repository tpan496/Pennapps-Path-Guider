#!/usr/bin/env python

import random
import socket, select
from time import gmtime, strftime
from random import randint

image = '/home/pi/Desktop/image.png'

HOST = '192.168.50.45'
PORT = 6666

sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_address = (HOST, PORT)
sock.connect(server_address)
while True:

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
    print 'answer = %s' % answer

    #myfile.close()
