# Hashtag Inspektor

## Die von Nico erhalte Anforderungen

Eure Idee gefällt uns sehr gut. Allerdings hätte eure App ohne die Analyse der Stimmung wenig Mehrwert. Daher wären die Mindestanforderungen an eure App Folgende:

- Die Suche nach Hashtags, inkl. der Auflistung der "dazugehörigen" Hashtags, muss funktionieren.
- Die Analyse der Stimmung muss auch funktionieren. Sollte dies nicht möglich sein, kommt noch mal auf uns zu. In dem Fall können wir zusammen noch mal überlegen, wie ihr basierend auf einer Hashtag-Suche einen Mehrwert erzeugen könnt.

Die Mindestanforderungen müsst ihr erfüllen, damit ihr den Kurs bestehen könnt - konzentriert euch also vor allem darauf.

Zusätzlich fänden wir gut, wenn ihr den Rest eures Konzeptes umsetzt.

Ein "Killer-Feature" wäre dann noch, wenn ihr bspw. Tweets über die App absenden könntet, die dann z. B. automatisch die 5 Hashtags einer Suche zusammen verwenden, die am häufigsten kombiniert werden.


Viele Grüße
Nicolas Autzen

## Ideen

1.    Tweets fuer einen Hashtag filtern nach:
*  Beliebtheit (Klicks, Likes…)
*  Sentiment 

2.    #-Liste verändern und in die Zwischenablage kopieren

## Probleme 
*  Die "Hashtag"-liste in Response kann manchmal leer sein, obwohl es eigtl. #'s im Tweet gibt.
*  Sprachunterstützung: https://cloud.google.com/natural-language/docs/languages (Was passiert wenn die Sprache des Tweets nicht unterstützt wird?)
*  Die Zahl der Query wird begrenzt

## JSON Requests and answers
### Request parameter we need
* lang (We should restrict the language to just english and german tweets)
* result_type (mixed, recent, popular: maybe defined by user)
* count (number of tweets per page, default:15, maximum:100)

### Answer parameters we need
* created_at
* id 
* text
* entities
    * ...hashtags
* user
    * ...name (e.g. Nasa)
    * screenname (@Nasa)
    * profile_image_url
* retweet_count
* favorite_count


