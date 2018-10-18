var scrape = require('website-scraper');
scrape({
  urls: ['http://www.gesetze-bayern.de'],
  directory: './files/rtf'
}, onresult);

function onresult(error, result){
  console.log("ready");
}