from datetime import datetime

from django.db import models


class HappyMoment(models.Model):
    text = models.TextField()
    filename = models.CharField(max_length=80, blank=True)
    color = models.CharField(max_length=80, blank=True)
    level = models.IntegerField(default=0, blank=True)
    latitude = models.FloatField(default=0, blank=True)
    longitude = models.FloatField(default=0, blank=True)
    speed = models.FloatField(default=0, blank=True)
    is_active = models.BooleanField(default=True)
    created_dt = models.DateTimeField(default=datetime.now)
    updated_dt = models.DateTimeField(default=datetime.now)

    def __unicode__(self):
        return self.text


# eof
