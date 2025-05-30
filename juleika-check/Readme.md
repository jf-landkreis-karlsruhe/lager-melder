## Get juleikas
> mysql> set NAMES 'utf8';
> mysql> select b.last_name, l.juleika_number, l.juleika_expire_date from youth_leaders l join base_attendees b on b.id = l.base_attendee_id where juleika_number <> '';


### Outputs
mysql> select y.juleika_number, b.first_name, b.last_name, d.name, d.phone_number, d.name_kommandant, d.phone_number_kommandant from youth_leaders y join base_attendees b on b.id = y.base_attendee_id join departments d on d.id = b.department_id  where y.juleika_number like '%' or y.juleika_number like '%';
