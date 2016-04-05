<!DOCTYPE HTML>
<html>
<head>
	
    
	<script type="text/javascript" src="<?php echo base_url("assets/js/canvasjs.min.js"); ?>"> </script>
  	<script type="text/javascript" src="<?php echo base_url("assets/js/jquery-1.11.2.min.js"); ?>"></script>

	<script type="text/javascript" src="<?php echo base_url("assets/js/bootstrap.js"); ?>"></script>

	<script type="text/javascript">
	window.onload = function () {

		var jArray = <?php echo json_encode($cats);?>;
		var jArray1 = <?php echo json_encode($pCounts);?>;
		
		
		//console.log(jArray1);
		var chart = new CanvasJS.Chart("chartContainer", {
			title: {
				text: "Problems Vs Category"
			},
			axisY: {
				labelFontSize: 12,
				labelFontColor: "dimGrey"
			},
			axisX: {
				labelAngle: -30
			},
			data: [
			{
				type: "column",
				dataPoints: [
				/*{ y: 10, label: "Dhanmondi" },
				{ y: 15, label: "Mirpur" },
				{ y: 25, label: "Palashi" },
				{ y: 30, label: "Lalbagh" },
				{ y: 28, label: "Uttara" }*/
				]
			}
			]
		});

		var offset = 20;
		var init = 0;
		for(var i=0;i<jArray1.length;i++){
			chart.options.data[0].dataPoints.push({x:init+=offset, y: parseInt(jArray1[i]), label: jArray[i][1]}); // Add a new dataPoint to dataPoints array
		}
		
		chart.render();

		var jArray2 = <?php echo json_encode($locations);?>;
		var jArray3 = <?php echo json_encode($lCounts);?>;
		
		
		console.log(jArray3);
		var chart1 = new CanvasJS.Chart("chartContainer1", {
			title: {
				text: "Problems Vs Locations"
			},
			axisY: {
				labelFontSize: 15,
				labelFontColor: "dimGrey"
			},
			axisX: {
				labelAngle: -30
			},
			data: [
			{
				type: "column",
				dataPoints: [
				/*{ y: 10, label: "Dhanmondi" },
				{ y: 15, label: "Mirpur" },
				{ y: 25, label: "Palashi" },
				{ y: 30, label: "Lalbagh" },
				{ y: 28, label: "Uttara" }*/
				]
			}
			]
		});

		var offset = 10;
		var init = 0;
		for(var i=0;i<jArray3.length;i++){
			chart1.options.data[0].dataPoints.push({ y: parseInt(jArray3[i]), label: jArray2[i]}); // Add a new dataPoint to dataPoints array
		}
		chart1.render();


	//chart1.render();
	}
	</script>
</head>

	<body>
			
			<div class="container-fluid">
				<div class="col-md-2"></div>
				<div class="col-md-8" id="chartContainer" style="height: 300px; margin-top: 20px width: 50%;"></div>
				<div class="col-md-2"></div>
			</div>
		<div class="container-fluid">
				<div class="col-md-2">.</div>
				<div class="col-md-8" id="chartContainer1" style="height: 300px; margin-top: 20px width: 50%;"></div>
				<div class="col-md-2"></div>
			</div>
		<!--
		
		<script type="text/javascript">
			/*var jArray = <?php 
							echo json_encode($catData);
						?>;
			var jArray1 = <?php 
							echo json_encode($teamData);
						?>;	*/
			var jArray = []; 
			var jArray1 = [];
			jArray[0] = []; jArray[1] = []; jArray[2] = []; jArray[3] = [];
			jArray1[0] = []; jArray1[1] = []; jArray1[2] = []; jArray1[3] = [];
			jArray[0]['catPoint']=120;
			jArray[0]['cat']="Open Dustbin";
			jArray[1]['catPoint']=50;
			jArray[1]['cat']="Open Manhole";
			jArray[2]['catPoint']=80;
			jArray[2]['cat']="Broken Bridge";
			jArray[3]['catPoint']=10;
			jArray[3]['cat']="occupied footpath";

			jArray1[0]['teamPoint']=30;
			jArray1[0]['team_name']="Dhanmondi";
			jArray1[1]['teamPoint']=17;
			jArray1[1]['team_name']="Palashi";
			jArray1[2]['teamPoint']=21;
			jArray1[2]['team_name']="Lalbagh";
			jArray1[3]['teamPoint']=41;
			jArray1[3]['team_name']="Uttara";
			window.onload = function () {
			var chart = new CanvasJS.Chart("chartContainer",
			{
			  title:{
				text: "prroblems per category"
			  },
			  data: [

			  {
				dataPoints: [
				{ x: 10, y: jArray[0]['catPoint'], label: jArray[0]['cat']},
				{ x: 20, y: jArray[1]['catPoint'], label: jArray[1]['cat'] },
				{ x: 30, y: jArray[2]['catPoint'], label: jArray[2]['cat']},
				{ x: 40, y: jArray[3]['catPoint'], label: jArray[3]['cat']}
				]
			  }
			  ]
			});

			chart.render();

			var chart1 = new CanvasJS.Chart("chartContainer1",
			{
			  title:{
				text: "problems Vs Area"
			  },
			  data: [

			  {
				dataPoints: [
					
				]
			  }
			  ]
			});
			
			var offset = 10;
			var init = 0;
			for(var i=0;i<jArray1.length;i++){
				chart1.options.data[0].dataPoints.push({x:init+=offset, y: jArray1[i]['teamPoint'], label: jArray1[i]['team_name']}); // Add a new dataPoint to dataPoints array
			}
			chart1.render();
			
		}
		  

		</script>-->

	</body>
</html>