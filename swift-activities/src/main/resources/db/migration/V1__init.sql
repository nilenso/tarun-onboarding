CREATE SCHEMA swift_activities;

CREATE TYPE swift_activities.activity_type AS ENUM('Running', 'Swimming', 'Cycling');

CREATE TABLE swift_activities.user_account (
	user_id uuid NOT NULL,
	name text NULL,
	created_at timestamptz DEFAULT now(),
	updated_at timestamptz DEFAULT now(),
	CONSTRAINT user_account_pk PRIMARY KEY (user_id)
);

CREATE TABLE swift_activities.activity (
	activity_id uuid NOT NULL,
	user_id uuid,
	name text NULL,
	type swift_activities.activity_type NOT NULL,
	start_time timestamptz NULL,
	end_time timestamptz NULL,
	created_at timestamptz DEFAULT now(),
	updated_at timestamptz DEFAULT now(),
	CONSTRAINT activity_pk PRIMARY KEY (activity_id)
);

CREATE TABLE swift_activities.activity_geolocation_data (
	activity_geo_data_id serial NOT NULL,
	activity_id uuid NOT NULL,
	data json NOT NULL,
	"timestamp" timestamptz NULL,
	created_at timestamptz DEFAULT now(),
	CONSTRAINT activity_log_pk PRIMARY KEY (activity_log_id),
	CONSTRAINT activity_log_fk FOREIGN KEY (activity_id) REFERENCES swift_activities.activity(activity_id)
);

CREATE TABLE swift_activities.activity_insights (
	activity_insight_id uuid NOT NULL,
	activity_id uuid NOT NULL,
	stats bytea NOT NULL,
	created_at timestamptz DEFAULT now(),
	CONSTRAINT activity_insights_pk PRIMARY KEY (activity_insight_id),
	CONSTRAINT activity_insights_fk FOREIGN KEY (activity_id) REFERENCES swift_activities.activity(activity_id)
);

