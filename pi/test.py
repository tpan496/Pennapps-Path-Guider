from picamera import PiCamera
from time import sleep

camera = PiCamera()

datapath = '/home/pi/Desktop/'
i = 0
while True:
    camera.capture(datapath + 'image'+str(i)+'.jpg')
    sleep(1)



