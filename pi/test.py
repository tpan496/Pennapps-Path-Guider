from picamera import PiCamera
from time import sleep

datapath = '/home/pi/Desktop/'
camera = PiCamera()
i = 0
while true:
    camera.capture(datapath + 'image'+i+'.jpg')
