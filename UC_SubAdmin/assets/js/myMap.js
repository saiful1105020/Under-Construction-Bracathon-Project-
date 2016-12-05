function myMap(a, b ) {
        
          var myLatLng = {lat: a , lng: b };

          var map = new google.maps.Map(document.getElementById('map'), {
            zoom: 8,
            center: myLatLng
          });
  
          var marker = new google.maps.Marker({
            position: myLatLng,
            map: map,
            title: 'Hello World!'
          });

          
        }

 function getLat()
      {
        var myLatLng = {lat: 23.8103 , lng: 90.4125 };
         
         var map = new google.maps.Map(document.getElementById('map2'), {
            zoom: 8,
            center: myLatLng
          });
        var marker;
        google.maps.event.addListener(map, 'click', function(event) {

           marker = new google.maps.Marker({position: event.latLng, map: map});
           $.ajax({
              type: "POST",
              url: "Admin.php",
              data: { lat: event.latLng.lat, lon: event.latlng.lng }
            })
              .done(function( msg ) {
                alert( "Data Saved: " + msg );
              });

            });
        
      }

      
 