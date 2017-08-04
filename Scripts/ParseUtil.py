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

		compactReport = {
			'address':str(loc),
			'author':'SpotCrime.com',
			'description': description,
			'date': date,		
		};


		dic = {'type': 'feed-crime', 'compactReports': compactReport, 'latitude':geo_lat, 'longitude':geo_long, 'phoneNumber':'0'}


		s = json.dumps(dic)
		file.write(s)
		if e != entries[-1]:
			file.write(',')
		i+=1
	file.write(']')
	file.close()


def parseWeather(v):
	source = v;
	filename = "weather.json"

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
		title = e.get('cap_event', '')
		summary = e.get('title', '')
		effectiveDate = e.get('cap_effective', '')
		expireDate = e.get('cap_expires', '')
		severity = e.get('cap_severity', '')
		link = e.get('link', '')
		detail = e.get('summary_detail','').get('value', '')
		area = e.get('cap_areadesc')
		
		compactReport = {
			'title': title,
			'summary': summary,
			'effectiveDate': effectiveDate,
			'expireDate': expireDate,
			'severity': severity,
			'link': link,
			'detail': detail,
			'area': area		
		};

		dic = {'type': 'feed-weather', 'compactReports': compactReport, 'latitude':0, 'longitude':0, 'phoneNumber':'0'}
		

		s = json.dumps(dic)
		file.write(s)
		if e != entries[-1]:
			file.write(',')
		i+=1
	file.write(']')
	file.close()



def parseMissingKid(v):
	return 0


