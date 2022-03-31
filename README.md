# read-and-learn-japanese
An android app that helps you learn Japanese through reading and SRS flashcards. 

## Description
This app is built to help you learn Japanese by creating an easy to use reader that will allow for quick translation and flashcard creation. \
\
I utilized an API provided by jisho.org to get the Japanese dictionary information. The dictionary function only works while connected to the internet.\
\
I used RoomDatabase to store user words and their status (known or unknown, flashcard interval and due date). 

### Light and Dark Mode
From the home screen you can access your texts as well as upload new texts using the floating action button. Uploaded texts are text only. Copy and paste text and add a 
title and they will appear on the home screen. \
\
![Light](https://github.com/gomezdevlopment/read-and-learn-japanese/blob/master/homeScreen.jpg)
![Dark](https://github.com/gomezdevlopment/read-and-learn-japanese/blob/master/homeScreenDark.jpg)

### Click on Words for Translation
Click and hold on new words with the selection tool to get the action menu. Click define to get the defintion. Click learn to make a flashcard of all marked definitions.
Click known to mark that word as known and remove it from your flashcards. Marked words will be highlighted as a clickable span to be referenced with one click as you 
read.\
\
![Click](https://github.com/gomezdevlopment/read-and-learn-japanese/blob/master/clickDemo.jpg)
![Popup](https://github.com/gomezdevlopment/read-and-learn-japanese/blob/master/popupDemo.jpg)

### Access your Flashcards from the Study Screen
Words marked as unknown will appear in this screen. Click study to start studying your due flashcards for that day. The flashcard function is a Spaced Repition System.
Clicking good will increase the interval at which you see that card (1 day -> 3 days -> 5 days, etc.) and clicking again will reset the interval to 1.\
\
![Study](https://github.com/gomezdevlopment/read-and-learn-japanese/blob/master/studyScreen.jpg)
![Flashcard](https://github.com/gomezdevlopment/read-and-learn-japanese/blob/master/flashcard.jpg)
![FlashcardResult](https://github.com/gomezdevlopment/read-and-learn-japanese/blob/master/flashcardResult.jpg)

### English-Japanese Dictionary
Look up words in English, Japanese, or romanji to get a definition using the API provided by jisho.org. \
\
![Dictionary](https://github.com/gomezdevlopment/read-and-learn-japanese/blob/master/dictionaryScreen.jpg)
