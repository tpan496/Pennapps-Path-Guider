#!/usr/bin/env python

import random
import socket, select
from time import gmtime, strftime
from random import randint

image = "/Users/liukaige/Desktop/test.png"

HOST = '127.0.0.1'
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
    if answer == 'GOT NAME':
        sock.sendall(bytes)

        # check what server send
        answer = sock.recv(4096)
        print 'answer = %s' % answer

        if answer == 'GOT IMAGE' :
            sock.sendall("BYE BYE ")
            print 'Image successfully send to server'

    myfile.close()

finally:
    sock.close()