# -*- coding: utf-8 -*-

import sys
reload(sys)
sys.setdefaultencoding('utf-8')

import io
import json
import oauth2client
import os
import httplib2
import urllib2
from apiclient import discovery
from oauth2client import client
from oauth2client import tools
from urllib import urlencode

import flask
app = flask.Flask(__name__)

try:
    import argparse
    flags = argparse.ArgumentParser(parents=[tools.argparser]).parse_args()
except ImportError:
    flags = None

SCOPES = 'https://www.googleapis.com/auth/drive'
CLIENT_SECRET_FILE = 'client_secret.json'
APPLICATION_NAME = 'SOMAReport Test'

@app.route("/")
def home():
    return "Hello World!"

@app.route('/authcallback/<int:did>')
def oauth2callback(did):
	flow = client.flow_from_clientsecrets(
    'client_secret.json',
    scope=SCOPES,
    redirect_uri=flask.url_for('oauth2callback', did=did, _external=True))
	flow.user_agent = APPLICATION_NAME
	if 'code' not in flask.request.args:
		auth_uri = flow.step1_get_authorize_url()
		return flask.redirect(auth_uri)
	else:
		auth_code = flask.request.args.get('code')
		print auth_code
		credentials = flow.step2_exchange(auth_code)
		with io.open(str(did)+'.json', 'w', encoding='utf-8') as f:
			f.write(unicode(credentials.to_json()))
    	return flask.redirect(flask.url_for('getFilelist', did=did))

@app.route("/drive/files/<int:did>")
def getFilelist(did):
	if checkCredential(did):
		credentials = getCredential(did)
		http = credentials.authorize(httplib2.Http())
		service = discovery.build('drive', 'v2', http=http)
		results = service.files().list(maxResults=3).execute()
		items = results.get('items', [])
		if not items:
			return 'No files found.'
		else:
			print_v = ""
			for item in items:
				print_v += ('{0} ({1}) \n '.format(unicode(item['title']), unicode(item['id'])))
			return print_v
	else:
		return flask.redirect(flask.url_for('oauth2callback', did=did))

def checkCredential(did):
	store = oauth2client.file.Storage(str(did)+'.json')
	credentials = store.get()
	if not credentials or credentials.invalid:
		return False
	else:
		return True

def getCredential(did):
	store = oauth2client.file.Storage(str(did)+'.json')
	credentials = store.get()
	return credentials

'''
def main():
    credentials = get_credentials()
    http = credentials.authorize(httplib2.Http())
    service = discovery.build('drive', 'v2', http=http)

    results = service.files().list(maxResults=10).execute()
    items = results.get('items', [])
    if not items:
        print 'No files found.'
    else:
        print 'Files:'
        for item in items:
            print '{0} ({1})'.format(unicode(item['title']), unicode(item['id']))
    # 파일 업로드
    data = open("test.txt", 'r') 
    resp, content = http.request("https://www.googleapis.com/upload/drive/v2/files?uploadType=media","POST",data)
	print resp
'''
if __name__ == '__main__':
	app.run(debug=True)