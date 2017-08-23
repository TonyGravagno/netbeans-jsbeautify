/*jshint curly:true, eqeqeq:true, laxbreak:true, noempty:false */
/*

Formatted comments that we wouldn't want auto-adjusted


    jslint_happy (default false) - if true, then jslint-stricter mode is enforced.

            jslint_happy        !jslint_happy
            ---------------------------------
            function ()         function()

            switch () {         switch() {
            case 1:               case 1:
              break;                break;
            }                   }



    js_beautify(js_source_text, {
      'indent_size': 1,
      'indent_char': '\t'
    });

*/
// Manually reformat this code with odd spaces and EOLs
// Then JSBeautify and see what happens.
// Object.values polyfill found here:
// http://tokenposts.blogspot.com.au/2012/04/javascript-objectkeys-browser.html
if( !Object.values )
{
    Object.values = function( o )
    {
        if( o !== Object( o ) )
        {
            throw new TypeError( 'Object.values called on a non-object' );
        }
        var k = [ ],
            p;
        for( p in o )
        {
            if( Object.prototype.hasOwnProperty.call( o, p ) )
            {
                k.push( o[ p ] );
            }
        }
        return k;
    };
}
( function( )
{
    function foo( a, b )
    {
        var aaa = {};
        var bbb;
        for( key in aaaa )
        {
            ///
        }
        return b;
    }
}( ) );