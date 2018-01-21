import io
import socket
import struct
from PIL import Image
from recognition import *
# all interfaces)
server_socket = socket.socket()
server_socket.bind(('192.168.50.45', 3333))
server_socket.listen(0)

# Accept a single connection and make a file-like object out of it
while True:
    print "yolo"
    # Start a socket listening for connections on 0.0.0.0:8000 (0.0.0.0 means
    conn = server_socket.accept()[0]
    connection = conn.makefile('rb')
    try:
        while True:
            print "wakaka"
            # Read the length of the image as a 32-bit unsigned int. If the
            # length is zero, quit the loop
            image_len = struct.unpack('<L', connection.read(struct.calcsize('<L')))[0]
            if not image_len:
                break
            # Construct a stream to hold the image data and read the image
            # data from the connection
            image_stream = io.BytesIO()
            image_stream.write(connection.read(image_len))
            # Rewind the stream, open it as an image with PIL and do some
            # processing on it
            image_stream.seek(0)
            image = Image.open(image_stream)
            image.save('/Users/liukaige/Desktop/img.png')
            print "3"
            name = str(conn.recv(4096))
            try:
                (a,b,c,d,e,f) = recognition('/Users/liukaige/Desktop/img.png',name)
            except:
                (a,b,c,d,e,f) = (0,0,0,0,0,0)
            print "4"
            conn.sendall(str(a) + " " + str(b) + " " + str(c) + " " + str(d) + " " + str(e) + " " + str(f))
    finally:
        connection.close()
