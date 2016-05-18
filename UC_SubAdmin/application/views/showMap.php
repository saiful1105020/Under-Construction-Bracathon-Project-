
    <script>
        function initMap( ) {
        var jArray = [];
        jArray[0] =23.8103; jArray[1] = 90.4125;
        jArray[2] = 22.3475; jArray[3] = 91.8123;
        jArray[4] = 24.9045; jArray[5] = 91.8611;
        jArray[6] = 24.3636; jArray[7] = 88.6241;
        jArray[8] = 22.7029; jArray[9] = 90.3466;
        jArray[10] = 25.7439 ; jArray[11] = 89.2572;
        jArray[12] = 22.8456; jArray[13] = 89.5403;
          var myLatLng = {lat: jArray[0] , lng: jArray[1] };

          var map = new google.maps.Map(document.getElementById('map'), {
            zoom: 8,
            center: myLatLng
          });
          var y=1;
          var z='vehicle'+y;
          
          var x=document.getElementById(z).checked;
          console.log(x);
        for(var i=0;i<6;i++)
        {
          myLatLng = {lat: jArray[i*2] , lng: jArray[i*2+1] };

          // var map = new google.maps.Map(document.getElementById('map'), {
          //   zoom: 10,
          //   center: myLatLng
          // });

          if(x)
          {
            var marker = new google.maps.Marker({
              position: myLatLng,
              map: map,
              title: ''
            }); 
          }
        }
      }
    </script>

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
                    <td><input type="checkbox" id="vehicle" value="1"> Broken Road<br></td>
                    <td><input type="checkbox" id="vehicle1" value="1"> Broken Road<br></td>
                    <td><input type="checkbox" id="vehicle2" value="1"> Broken Road<br></td>
                    <td><input type="checkbox" id="vehicle3" value="1"> Broken Road<br></td>
                    <td><input type="checkbox" id="vehicle4" value="1"> Broken Road<br></td>
                    <td><input type="checkbox" id="vehicle5" value="1"> Broken Road<br></td>



                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="row">
        <div class="col-md-5"></div>
        <div class="col-md-5">
            <button class="btn btn-danger" type="submit" onclick="initMap()">Show On Map</button>
        </div>
        <div class="col-md-2"></div>
    </div>
    <br>
    <div id="map" ></div>
            
        
    
        

    <script async defer
    src="https://maps.googleapis.com/maps/api/js?key=AIzaSyC4LGcxa0bb_eUxxLYVv1sO6NRZ6UbPGsc&callback=initMap">
    </script>
  </body>
</html>