from picamera import PiCamera
from time import sleep

camera = PiCamera()

datapath = '/home/pi/Desktop/'
i = 0
while true:
    camera.capture(datapath + 'image'+i+'.jpg')
    sleep(1000)



