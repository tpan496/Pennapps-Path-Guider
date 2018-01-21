import boto3

BUCKET = "lolaguagua"
KEY = "WechatIMG3.jpeg"

def detect_labels(bucket, key, max_labels=10, min_confidence=90, region="us-east-2"):
	rekognition = boto3.client("rekognition", region, aws_access_key_id="AKIAJ7NGTEGGVXO7V62A",aws_secret_access_key="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")
	response = rekognition.detect_labels(
		Image={
			"S3Object": {
				"Bucket": bucket,
				"Name": key,
			}
		},
		MaxLabels=max_labels,
		MinConfidence=min_confidence,
	)
	return response['Labels']


for label in detect_labels(BUCKET, KEY):
	print("{Name} - {Confidence}%".format(**label))