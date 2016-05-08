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
				text: "Category vs Problems"
			},
			axisY: {
				labelFontSize: 20,
				labelFontColor: "dimGrey"
			},
			axisX: {
				labelAngle: -30
			},
			axisY:{
		        title:"# of Problems"   
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
				text: "Locations Vs Problems"
			},
			axisY: {
				labelFontSize: 12,
				labelFontColor: "dimGrey"
			},
			axisX: {
				labelAngle: -30
			},
			axisY:{
		        title:"# of Problems"   
		    },
			data: [
			{
				type: "column",
				dataPoints: [
				
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

		///location with comparison
		var chart5 = new CanvasJS.Chart("chartContainer5",
		    {
		      
		      animationEnabled: true,
		      title:{
		        text: "Location Vs Problems (with comparison)",
		        fontSize: 30
		      },
		      toolTip: {
		        shared: true
		      },      
		      axisY: {
		        title: "# of problems",
		        labelFontColor: "dimGrey"
		      },
		      axisX: {
				labelFontSize: 15,
				labelAngle: -30
			  },
		            
		      data: [ 
		      {
		        type: "column", 
		        color: "#C24642",
		        name: "Total Problems",
		        legendText: "Total Problems",
		
		        showInLegend: true,
		        dataPoints:[
		       
		        ]
		      },
		      {
		        type: "column",
		        color: "#369EAD",
		        name: "Solved Problems",
		        legendText: "Solved Problems",
		        showInLegend: true, 
		        dataPoints:[
		        
		        ]
		      },
		      
		      ],
		          legend:{
		            cursor:"pointer",
		            itemclick: function(e){
		              if (typeof(e.dataSeries.visible) === "undefined" || e.dataSeries.visible) {
		                e.dataSeries.visible = false;
		              }
		              else {
		                e.dataSeries.visible = true;
		              }
		              chart.render();
		            }
		          },
		        });
		for(var i=0;i<jArray2.length;i++){
			chart5.options.data[0].dataPoints.push({y: parseInt(jArray3[i]), label: jArray2[i]}); // Add a new dataPoint to dataPoints array
		}
		for(var i=0;i<jArray2.length;i++){
			chart5.options.data[1].dataPoints.push({ y: parseInt(jArray3[i]), label: jArray2[i]}); // Add a new dataPoint to dataPoints array
		}
		chart5.render();
		///new chart
		/*var chart3 = new CanvasJS.Chart("chartContainer3",
		    {
		      title:{
		      text: "Category Vs Problems"   
		      },
		      axisY: {
				labelFontSize: 12,
				labelFontColor: "dimGrey"
			  },
			  axisX: {
				labelAngle: -30
			  },
		      axisY:{
		        title:"# of Problems"   
		      },
		      animationEnabled: true,
		      data: [
		      {        
		        type: "stackedColumn",
		        toolTipContent: "{label}<br/><span style='\"'color: {color};'\"'><strong>{name}</strong></span>: {y} Problems",
		        name: "Solved Problems",
		        showInLegend: "true",
		        dataPoints: [
		        
		        ]
		      
				},  {        
		        type: "stackedColumn",
		        toolTipContent: "{label}<br/><span style='\"'color: {color};'\"'><strong>{name}</strong></span>: {y} Problems",
		        name: "Total Reported",
		        showInLegend: "true",
		        dataPoints: [
		       
		        ]
		      }            
		      ]
		      ,
		      legend:{
		        cursor:"pointer",
		        itemclick: function(e) {
		          if (typeof (e.dataSeries.visible) ===  "undefined" || e.dataSeries.visible) {
			          e.dataSeries.visible = false;
		          }
		          else
		          {
		            e.dataSeries.visible = true;
		          }
		          //chart3.render();
		        }
		      }
		    });
			for(var i=0;i<jArray1.length;i++){
			chart3.options.data[1].dataPoints.push({y: parseInt(jArray1[i]), label: jArray[i][1]}); // Add a new dataPoint to dataPoints array
			}
			for(var i=0;i<jArray3.length;i++){
			chart3.options.data[0].dataPoints.push({ y: parseInt(jArray3[i]), label: jArray[i][1]}); // Add a new dataPoint to dataPoints array
			}
			
			
		    chart3.render();*/

		    ///one more new chart

		    var chart4 = new CanvasJS.Chart("chartContainer4",
		    {
		      
		      animationEnabled: true,
		      title:{
		        text: "Category Vs Problems (with comparison)",
		        fontSize: 30
		      },
		      toolTip: {
		        shared: true
		      },      
		      axisY: {
		        title: "# of problems",
		        labelFontColor: "dimGrey"
		      },
		      axisX: {
				labelFontSize: 15,
				labelAngle: -30
			  },
		            
		      data: [ 
		      {
		        type: "column", 
		        color: "#C24642",
		        name: "Total Problems",
		        legendText: "Total Problems",
		
		        showInLegend: true,
		        dataPoints:[
		       
		        ]
		      },
		      {
		        type: "column",
		        color: "#369EAD",
		        name: "Solved Problems",
		        legendText: "Solved Problems",
		        showInLegend: true, 
		        dataPoints:[
		        
		        ]
		      },
		      
		      ],
		          legend:{
		            cursor:"pointer",
		            itemclick: function(e){
		              if (typeof(e.dataSeries.visible) === "undefined" || e.dataSeries.visible) {
		                e.dataSeries.visible = false;
		              }
		              else {
		                e.dataSeries.visible = true;
		              }
		              chart.render();
		            }
		          },
		        });
		for(var i=0;i<jArray1.length;i++){
			chart4.options.data[0].dataPoints.push({y: parseInt(jArray1[i]), label: jArray[i][1]}); // Add a new dataPoint to dataPoints array
		}
		for(var i=0;i<jArray3.length;i++){
			chart4.options.data[1].dataPoints.push({ y: parseInt(jArray3[i]), label: jArray[i][1]}); // Add a new dataPoint to dataPoints array
		}
		console.log(jArray3.length, jArray1.length);
		for(var i=5;i<10;i++){
			chart4.options.data[1].dataPoints.push({ y: 0, label: jArray[i][1]}); // Add a new dataPoint to dataPoints array
		}
		chart4.render();

	//chart1.render();
	}
	</script>
</head>

	<body>
			<div class="container-fluid">
				<div class="col-md-2">.</div>
				<div class="col-md-8" id="chartContainer1" style="height: 300px; margin-top: 20px width: 60%;"></div>
				<div class="col-md-2"></div>
			</div>
			<div class="container-fluid">
				<div class="col-md-2">.</div>
				<div class="col-md-8" id="chartContainer5" style="height: 300px; margin-top: 20px width: 60%;"></div>
				<div class="col-md-2"></div>
			</div>
			<div class="container-fluid">
				<div class="col-md-2"></div>
				<div class="col-md-8" id="chartContainer" style="height: 400px; margin-top: 20px width: 60%;"></div>
				<div class="col-md-2"></div>
			</div>
			<div class="container-fluid">
				<div class="col-md-2">.</div>
				<div class="col-md-8" id="chartContainer4" style="height: 400px; margin-top: 20px width: 60%;"></div>
				<div class="col-md-2"></div>
			</div>
			
			<!--
			<div class="container-fluid">
				<div class="col-md-2">.</div>
				<div class="col-md-8" id="chartContainer3" style="height: 400px; margin-top: 20px width: 60%;"></div>
				<div class="col-md-2"></div>
			</div>-->
			
			<br><br><br><br>
			
		

	</body>
</html>