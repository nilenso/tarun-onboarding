# DB Schema

## User
| user_account | | |
|------|----|---|
|user_id| uuid| |
|created_at|timestamptz| default now() |
|updated_at|timestamptz| default now() |

## Activity
|activity| | |
|--------|----|---|
|activity_id | uuid | |
|name | string | optional |
|user_id | uuid| |
|activity_type|enum| |
|start_time|timestamptz| |
|end_time|timestamptz| optional |
|created_at|timestamptz| default now() |
|updated_at|timestamptz| default now() |

## ActivityGeolocationData
Table to store geolocation data captured from device while performing `activity`
| activity_geolocation_data | | |
|-------|-----|---|
|geolocation_data_id | serial | |
|activity_id | uuid | |
|data | json | |
|timestamp | timestamptz | |
|created_at|timestamptz| default now() |

`data` here would have the data returned by the device. `latitude` and `longitude` fields are mandatory. Other fields are optional
```
{ latitude: double
, longitude: double
, accuracy: int
, speed: int
}
```

## ActivityInsights
This stores the computed data for an activity. 
| activity_insights | | |
|------|---|---|
|activity_insight_id | uuid | |
|activity_id | uuid | |
| stats | bytea | |
|created_at|timestamptz| default now() |

`stats` would be a json object with data like:
```
{ duration: in seconds
, distance: in meters
, top_speed: in m/s 
, average_speed: in m/s 
, lowest_speed: in m/s}
```
