from django.contrib import admin

from happymoments.models import HappyMoment


class HappyMomentAdmin(admin.ModelAdmin):
    list_display = ('text', 'filename', 'created_dt')


admin.site.register(HappyMoment, HappyMomentAdmin)


# eof
