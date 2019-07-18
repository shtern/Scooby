import numpy as np
import json
import requests
import pandas as pd
from geopy import distance
from sklearn.cluster import DBSCAN
from sklearn.cluster import KMeans
from flask import Flask
app = Flask(__name__)
centers = []

def read_df(path='dataWindBird.csv'):
    return pd.read_csv(path, names=['provider','boardId','boardNo','latitude','longitude','lastReportedTime','estimatedRange','vol'])


def enrich(coords):
    r = requests.get('http://dev.virtualearth.net/REST/V1/Routes/LocalInsights?Waypoint={}&TravelMode=Walking&Optimize=time&MaxTime=5&TimeUnit=Minute&type=eatDrink&key=Ap3fCjQ15qTPpD-WTBNUgE_5ACiG3zqQZwELGEAtp8kRMd93XL9uj7390f_qvu6k'.format(coords))
    if r.status_code == requests.codes.ok:
        json_places = json.loads(r.content.decode('utf-8'))
        big_resources = json_places.get('resourceSets',[])
        if len(big_resources) > 0:
            resources = big_resources[0].get('resources',[])
            if len(resources) > 0:
                categoryTypeResults = resources[0].get('categoryTypeResults',[])
                if len(categoryTypeResults) > 0:
                    entities = categoryTypeResults[0].get('entities',[])
                    return len(entities)
    return 0

def get_centers(df):
    n = 7
    clusters_st = DBSCAN(eps=0.0001, min_samples=n).fit_predict(df[['latitude', 'longitude']])
    num_clusters = len(np.unique(clusters_st))
    df['street_label'] = clusters_st
    
    df['std_vol_small'] = (df.groupby('boardId')['vol'].transform('std') < 3) & (df.groupby('boardId')['boardId'].transform('count') > 10)
    
    kmeans = KMeans(n_clusters=num_clusters, random_state=0).fit(df[['latitude', 'longitude']])
    labels = kmeans.labels_
    centers = kmeans.cluster_centers_
    df['location_label'] = labels
    label, count_label = np.unique(labels, return_counts=True)
    df_centers = pd.DataFrame(data=centers, columns=['latitude', 'longitude'])
    df_centers['enrich_str'] = [str(row.latitude) + ',' + str(row.longitude) for index, row in df_centers.iterrows()]
    df_centers['num_entities'] = df_centers['enrich_str'].apply(enrich)
    m =  100
    df_centers['count_label_scale'] = m * (count_label - np.min(count_label))/(np.max(count_label) - np.min(count_label))
    df_centers['num_entities_scale'] = m * (df_centers['num_entities'] - df_centers['num_entities'].min())/(df_centers['num_entities'].max() - df_centers['num_entities'].min())
    df_centers['weight'] = 0.5 * (df_centers['count_label_scale'] + df_centers['num_entities_scale'])
    
    standing_loc = df[df['std_vol_small']][['latitude', 'longitude']]
    
    clusters_standing = DBSCAN(eps=0.0001, min_samples=n).fit_predict(standing_loc)
    num_clusters = len(np.unique(clusters_standing))
    kmeans = KMeans(n_clusters=num_clusters, random_state=0).fit(standing_loc)
    centers_standing = kmeans.cluster_centers_
    df_centers_standing = pd.DataFrame(data=centers_standing, columns=['latitude', 'longitude'])
    
    df_centers_standing['enrich_str'] = [str(row.latitude) + ',' + str(row.longitude) for index, row in df_centers_standing.iterrows()]
    df_centers_standing['num_entities'] = df_centers_standing['enrich_str'].apply(enrich)
    
    df_centers_standing['weight'] = m * (df_centers_standing['num_entities'] - df_centers_standing['num_entities'].min())/(df_centers_standing['num_entities'].max() - df_centers_standing['num_entities'].min())
    
    for ind1 in range(df_centers.shape[0]):
        weight = df_centers['weight'][ind1]
        for ind in range(df_centers_standing.shape[0]):
            val1 = tuple(df_centers_standing[['latitude', 'longitude']].values[ind])
            val2 = tuple(df_centers[['latitude', 'longitude']].values[ind1])
            if distance.distance(val1, val2).km < 0.1:
                weight -= df_centers_standing['weight'][ind]
        if weight < 0:
            weight = 0        
        df_centers['weight'][ind1] = weight
    return df_centers[['latitude', 'longitude','weight']]


def get_json(centers):
    result = []
    for index, coord_pair in centers.iterrows():
        item = dict()
        cluster = dict()
        cluster['lat']=coord_pair[0]
        cluster['lon']=coord_pair[1]
        item['cluster']=cluster
        item['text']='Parking score is: ' + str(coord_pair['weight'])
        result.append(item)
    return json.dumps({'result':result})

@app.route('/prepare_data')
def prepare_data():
    global centers
    df = read_df()
    centers=get_centers(df)
    return 'Success'

@app.route('/do_magic/<int:clusters_count>')
def do_magic(clusters_count):
    global centers
    return get_json(centers[:clusters_count])

@app.route('/')
def hello_world():
    return 'Hello!'
