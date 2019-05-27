# Mobile App Development

## 1. Idee -- #-Inspektor

Die App ermöglicht es, nach Hashtags (#) auf Twitter zu suchen und alle mit dem gesuchten # in Verbindung stehenden tags zu finden.
Sucht man z.B. nach #Apfelbaum, so listet die App alle #'s auf, die zusätzlich zu #Apfelbaum erwähnt werden und sortiert das Ergebnis nach Häufigkeit.
Die resultierende Liste könnte beispielsweise so aussehen:

"#Apfelbaum"

1. #Obst 60x erwähnt
2. #Essen 35x erwähnt
3. #Gesund 12x erwähnt

Die Suche kann allgemein, ohne bestimmte Parameter stattfinden oder wir schränken die Suche auf einen bestimmten Ort oder ein bestimmtes Land ein. 
Allgemein können wir nur Stichprobenartig suchen, da es zu manchen #'s Millionen von Tweets gibt.

## 2. Idee -- # Sentiment Analyzer

Unsere zweite Idee wäre, dass wir auch gerne mit der ersten Idee zusammenstellen wollen, ein Sentiment Analyse Tool.
Damit können wir beispielsweise nach einem Begriff suchen und die App liefert dann die Analyse zurück.
Als Beispiel: Der Benutzer gibt in der App den Begriff "adidas" und dann die Suche wird gestartet, danach sammeln wir alle Tweets. Die App beging die Tweets zu parsen und schicken wir das Resultat des Parsens an den Analyse-Tool weiter (entweder https://cloud.google.com/natural-language/ oder http://text-processing.com/docs/sentiment.html), um diese Tweets bezüglich "Sentiment-Value" zu analysieren.
Die App verfügt auch über die Möglichkeit, die Zahl der zu analysierenden Tweets auszuwählen, also z.B. die letzten 100 Tweets oder 1000 Tweets usw.