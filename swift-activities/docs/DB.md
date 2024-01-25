# DB Schema

| User | | |
|------|----|---|
|user_id| uuid| |
|created_at|timestamptz| default now() |
|updated_at|timestamptz| default now() |

|Activity| | |
|--------|----|---|
|activity_id | uuid | |
|name | string | optional |
|user_id | uuid| |
|activity_type|enum| |
|start_time|timestamptz| |
|end_time|timestamptz| optional |
|created_at|timestamptz| default now() |
|updated_at|timestamptz| default now() |

| ActivityGeolocationData | | |
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

| ActivityInsights | | |
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

|duratiom | int |
| average_speed | double |
| top_speed | double |
| lowest_speed | double |
