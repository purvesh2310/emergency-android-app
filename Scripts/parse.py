import urllib, json


url = "https://trainingtrack.hsema.dc.gov/NRss/RssFeed/AlertDCList"
response = urllib.urlopen(url)
data = json.loads(response.read())
print data
