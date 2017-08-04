import feedparser
import json
import sys
from pygeocoder import Geocoder
import time
import ParseUtil as util



link = {}
#link['spotcrime'] = "http://s3.spotcrime.com/cache/rss/sanjose.xml"
link['weather'] = "https://alerts.weather.gov/cap/ca.php?x=0"
#link['missingKid'] = "http://www.missingkids.org/missingkids/servlet/XmlServlet?act=rss&LanguageCountry=en_US&orgPrefix=NCMC&state=CA"

#"""
parser = feedparser.parse(link['weather'])
entries = parser.entries
e = entries[0]
#print(e.keys())
#print(e.items())
c = 0
for i in e.items():
	c+=1
	print('{} -- {}'.format(c,i))

#"""
"""
for k, v in link.items():
	sourceName = k
	sourceLink = v
	if sourceName == 'spotcrime':
		util.parseSpotCrime(v)
	elif sourceName == 'weather':
		util.parseWeather(v)
	else:
		util.parseMissingKid(v)
"""
	