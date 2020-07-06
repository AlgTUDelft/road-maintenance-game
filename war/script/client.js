/**
 * This file contains auxiliary functions for GWT web pages
 * 
 * @author Joris Scharpff 
 * @date 25 Feb 2013
 * @copyright 2013, Almende B.V.    
 */ 

/**
 * Tries to load the css that is most tailored to the client's viewport size
 */
function loadCSS( prefix ) {
  // get viewport height and widht
  var height = document.documentElement.clientHeight;
  var width = document.documentElement.clientWidth;
  
  //alert( width + " x " + height );
  
  loadCSSFile( prefix + ".css" );
} 

/**
 * Loads a the specified CSS file
 * 
 * @param css   
 */
function loadCSSFile( filename ) {
  var fileref = document.createElement( "link" );
  fileref.setAttribute( "rel", "stylesheet" );
  fileref.setAttribute( "type", "text/css" );
  fileref.setAttribute( "href", filename );
}