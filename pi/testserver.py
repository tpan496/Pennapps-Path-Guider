import socket

HOST = '192.168.50.45'
PORT2 = 3000
path = "/Users/liukaige/Desktop/test.png"

serversock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
serversock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
serversock.bind((HOST, PORT2))
serversock.listen(10)
sock, client_address = serversock.accept()
sock.sendall("name!!!!!!!")
file = open(path,"rb")
bytes = file.read()
sock.sendall(bytes)