import random
import socket, select
from time import gmtime, strftime
from random import randint

image = "tux.png"

HOST = '192.168.50.64/24'
PORT = 6666

sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_address = (HOST, PORT)
sock.connect(server_address)

try:

    # open image
    # send image size to server
    sock.sendall("FIND wakaka")
    answer = sock.recv(4096)
    print 'answer = %s' % answer
    answer = sock.recv(4096)
    print 'answer = %s' % answer

    answer = sock.recv(4096)
    print 'answer = %s' % answer

    answer = sock.recv(4096)
    print 'answer = %s' % answer

    answer = sock.recv(4096)
    print 'answer = %s' % answer

    answer = sock.recv(4096)
    print 'answer = %s' % answer

    answer = sock.recv(4096)

    print 'answer = %s' % answer


finally:
    sock.close()