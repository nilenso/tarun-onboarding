CREATE SCHEMA IF NOT EXISTS swift_activities;

do $$
begin
  CREATE TYPE swift_activities.activity_type AS ENUM('Running', 'Swimming', 'Cycling');
exception
  when duplicate_object then null;
end$$;

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
	geolocation_data_id uuid NOT NULL,
	activity_id uuid NOT NULL,
	data json NOT NULL,
	recorded_at timestamptz NOT NULL,
	created_at timestamptz DEFAULT now(),
	CONSTRAINT activity_geolocation_data_pk PRIMARY KEY (geolocation_data_id),
	CONSTRAINT activity_geolocation_data_fk FOREIGN KEY (activity_id) REFERENCES swift_activities.activity(activity_id)
);

CREATE TABLE swift_activities.activity_insights (
	activity_id uuid NOT NULL,
	stats json NOT NULL,
	created_at timestamptz DEFAULT now(),
	CONSTRAINT activity_insights_pk PRIMARY KEY (activity_id),
	CONSTRAINT activity_insights_fk FOREIGN KEY (activity_id) REFERENCES swift_activities.activity(activity_id)
);

