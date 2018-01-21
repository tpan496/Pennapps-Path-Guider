#!/usr/bin/env python

import random
import socket, select
from time import gmtime, strftime
from random import randint

import os

from recognition import *

imgcounter = 1
basename = "image%s.png"

HOST = '192.168.50.45'
PORT = 6666

connected_clients_sockets = []

server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
server_socket.bind((HOST, PORT))
server_socket.listen(10)

connected_clients_sockets.append(server_socket)

while True:

    read_sockets, write_sockets, error_sockets = select.select(connected_clients_sockets, [], [])
    name  = ""
    size = 0
    for sock in read_sockets:

        if sock == server_socket:
            print "lolo"
            sockfd, client_address = server_socket.accept()
            connected_clients_sockets.append(sockfd)

        else:
            def read_line(s):
                ret = ''
                while True:
                    c = s.recv(1)
                    if c == '\n' or c == '':
                        break
                    else:
                        ret += c
                return ret
            data = read_line(sock)
            txt = str(data)
            if data:
                if data.startswith('NAME'):
                    print "case 1"
                    tmp = txt.split()
                    name = tmp[1]
                    size = int(tmp[2])
                    sock.sendall("GOT NAME")
                else :
                    print "case 2"
                    myfile = open('/Users/liukaige/Desktop/WechatIMG3.png', 'wb')
                    myfile.write(data)
                    presize = len(data)
                    data = sock.recv(size - len(data))
                    myfile.write(data)
                    myfile.close()

                    #(x0,y0,x1,y1,w,h) = recognition("/Users/liukaige/Desktop/img.png",name)
                    sock.sendall("100 100 100 100 100 100")

        imgcounter += 1
#server_socket.close()