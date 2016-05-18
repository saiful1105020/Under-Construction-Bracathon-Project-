<!DOCTYPE HTML>
<html>
<head>
    
    <script type="text/javascript" src="<?php echo base_url("assets/js/jquery-1.11.2.min.js"); ?>"></script>

    <script type="text/javascript" src="<?php echo base_url("assets/js/bootstrap.js"); ?>"></script>

    <script type="text/javascript">
     function initMyMap( ) {
        
        var mapArray = <?php echo json_encode($mapData);?>;
        var radioArray = [];
        var selectArray = [];
        var index=0;
        for(var j=0;j<mapArray.length;j++)
        {
            radioArray[j] = "cat"+mapArray[j]['id'];
            selectArray[j] = document.getElementById(radioArray[j]).checked;
            if(selectArray[j])
              index = mapArray[j]['id'];
        }
        if(index==-1)
          index=0;

        var latArray = [];
        var lonArray = [];
       var locLength = mapArray[index]['locations'].length;
        for(var p=0; p<locLength; p++)
        {
          latArray[p] =  parseFloat(mapArray[index]['locations'][p]['lat']);
          lonArray[p] = parseFloat(mapArray[index]['locations'][p]['lon']);
        }
        console.log(latArray);
        var myLatLng = {lat: 23.8103 , lng: 90.4125 };
        var map = new google.maps.Map(document.getElementById('map'), {
          zoom: 8,
          center: myLatLng
        });
        
        for(var i=0;i<locLength;i++)
        {
          myLatLng = {lat: latArray[i] , lng: lonArray[i] };

          var marker = new google.maps.Marker({
            position: myLatLng,
            map: map,
            title: ''
          }); 
        }
      }
      </script>
      </head>

    <body>



    <br><br>
    <h2 style="text-align:center"><b>Show Problems in Map</b></h2>  

   <div class="row" class="col-md-4">
        <table class="table">
            <thead>
                <tr>
                    <td></td>
                    <td></td>
                </tr>
            </thead>
            <tbody>
		
                <tr>
                    <td></td>
					<?php 
						foreach($mapData as $e)
						{
							echo '<td><input type="radio" name="showCat" id="cat'.$e['id'].'" value="'.$e['id'].'"> ' . $e['name'].'<br></td>';
						}
					?>
					

                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="row">
        <div class="col-md-5"></div>
        <div class="col-md-5">
            <button class="btn btn-danger" type="submit" onclick="initMyMap()">Show On Map</button>
        </div>
        <div class="col-md-2"></div>
    </div>
    <br>
    <div id="map" ></div>
            
        
    
        

    <script async defer
    src="https://maps.googleapis.com/maps/api/js?key=AIzaSyC4LGcxa0bb_eUxxLYVv1sO6NRZ6UbPGsc&callback=initMyMap">
    </script>
  </body>
</html>