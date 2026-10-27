# TCGCardShare
TCGCardShare is an application that allows users to easily share their Magic the Gathering decks with each other.

It has an array of features including:
Creation of a deck list:
  
  -Modern Materials3 design is used for the creation of the list and the application
    +Materials3 allows the application to use the image of the card and an art preview when adding them to a deck.*
 
  -A user can search for cards in real time and add them to a deck
    >*this includes all of the statistics of the card since they are all fetched (30 at a time)*
 
  -A user can edit how many copies of each card are added to the deck
    >*By tapping the + or - the user can fully remove or increment the number of copies of a card in a deck*
  

    
Editing of a deck list:
  -A user can edit their own deck lists at any time:
    -The application makes sure that you own the list so no one can edit your lists
    -A user can delete a deck if it is no longer assembled
    -A user can import their ChatGPT API code in order to get AI help with their deck list
    
import deck feature:
  -supports formats from the most popular deck building sites
   -Included sites/formats
     -Moxfield,Manabox,MTGOnline,Scryfall,TappedOut
Custom built QR system
  -only fetches decks using the application it is tied to a unique ID and does not traverse the web through a link
  -Generates when a deck is built or imported
  
