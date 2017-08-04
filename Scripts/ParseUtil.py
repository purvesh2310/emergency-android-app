import feedparser
import json
import sys
from pygeocoder import Geocoder
import time

def parseDate(date):
	if date == "": return ""
	return date[:16]


def parseSpotCrime(v):
	source = v;
	filename = "spotcrime.json"

	#open file
	file = open(filename,'w')

	#parse feed
	parser = feedparser.parse(source)

	#entries are <item> tags
	entries = parser.entries

	# [ ] for json file
	file.write('[')
	i = 0
	for e in entries:
		if i%5 == 0:
			time.sleep(1)
		description = e.get('description', "")
		date = parseDate(e.get('published', ""))
		geo_long = e.get('geo_long',"")
		geo_lat = e.get('geo_lat',"")
		loc = ""
		if geo_long!="" and geo_lat!="":
			loc = Geocoder.reverse_geocode(float(geo_lat), float(geo_long))
			print(loc)
		if len(e.enclosures):
			image = e.enclosures[0].get('href',"")
		else:
			image = ""

		dic = {'address':str(loc),
			   'agency':'Yes',
			   'alertId': '',
			   'author':'SpotCrime.com',
			   'authorId':'',
			   'category':'Crime',
			   'comment':0,
			   'confirmation':0,
			   'description': description,
			   'endDate': date,
			   'image': image,
			   'latitude': geo_long,
			   'longitude': geo_lat,
			   'share':0,
			   'startDate':date,
			   'time':''
			   }

		s = json.dumps(dic)
		file.write(s)
		if e != entries[-1]:
			file.write(',')
		i+=1
	file.write(']')
	file.close()


def parseWeather(v):
	return 0



def parseMissingKid(v):
	return 0
