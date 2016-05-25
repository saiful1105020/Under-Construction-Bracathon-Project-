<!DOCTYPE HTML>
<html>
<head>
    
    <script type="text/javascript" src="<?php echo base_url("assets/js/jquery-1.11.2.min.js"); ?>"></script>

    <script type="text/javascript" src="<?php echo base_url("assets/js/bootstrap.js"); ?>"></script>

    <script type="text/javascript">
		window.onload = function ( ) {
			var para = <?php echo json_encode($location);?>;
			var a = parseFloat(para['lat']);
			var b=parseFloat(para['lon']);
          var myLatLng = {lat: a , lng: b };

          var map = new google.maps.Map(document.getElementById('map'), {
            zoom: 18,
            center: myLatLng
          });
  
          var marker = new google.maps.Marker({
            position: myLatLng,
            map: map,
            title: 'Problem!'
          });

          
        }
      </script>
      </head>

    <body>


    <h2 style="text-align:center"><b>Showing Problem Location in Map</b></h2>  

   
    <br>
    <div id="map" ></div>
            
        
    
        

    <script async defer
    src="https://maps.googleapis.com/maps/api/js?key=AIzaSyC4LGcxa0bb_eUxxLYVv1sO6NRZ6UbPGsc&callback=">
    </script>
  </body>
</html>