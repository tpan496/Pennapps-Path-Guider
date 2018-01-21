import random
import socket, select
from time import gmtime, strftime
from random import randint
from recognition import *

HOST = '192.168.50.45'
PORT = 3000

sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_address = (HOST, PORT)
sock.connect(server_address)
path = "/Users/liukaige/Desktop/img.png"
while True:
    name = sock.recv(4096)
    data = sock.recv(4096)
    myfile = open(path, 'wb+')
    myfile.write(data)
    myfile.close()
    print "haha"
    result = recgnition(name,path)
    sock.sendall(str(1))
