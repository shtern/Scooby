import numpy as np
import json
import pandas as pd
from sklearn.cluster import DBSCAN
from sklearn.cluster import KMeans
from flask import Flask
app = Flask(__name__)


def read_df(path='data15_7pointsVol11.csv'):
    return pd.read_csv(path, names=['boardId','boardNo','latitude','longitude','lastReportedTime','estimatedRange','vol'])


def get_centers(df):
    clusters_st = DBSCAN(eps=0.001, min_samples=1).fit_predict(df[['latitude', 'longitude']])
    num_clusters = len(np.unique(clusters_st))
    df['street_label'] = clusters_st
    kmeans = KMeans(n_clusters=num_clusters, random_state=0).fit(df[['latitude', 'longitude']])
    labels = kmeans.labels_
    centers = kmeans.cluster_centers_
    return centers


def get_json(centers):
    result = []
    for coord_pair in centers:
        item = dict()
        cluster = dict()
        cluster['lat']=coord_pair[0]
        cluster['lon']=coord_pair[1]
        item['cluster']=cluster
        item['text']='asdflasdkmfalsk'
        result.append(item)
    return json.dumps({'result':result})


@app.route('/do_magic/<int:clusters_count>')
def do_magic(clusters_count):
    df = read_df()
    centers=get_centers(df)
    return get_json(centers[:clusters_count])

@app.route('/')
def hello_world():
    return 'Hello!'
