
<div class="table-responsive">

	<h3>Search Reported Posts</h3>
     
	<div class="table-responsive" >
		<table class="table ">
			
			<tbody>
			<!--
				<tr height="25%">
					<td> </td>
					<td> </td>
					<td> </td>
					<td> </td>
				</tr>
				
				<tr height="25%">
					<td> </td>
					<td> </td>
					<td> </td>
					<td> </td>
				</tr>
				
				<tr height="25%">
					<td> </td>
					<td> </td>
					<td> </td>
					<td> </td>
				</tr>
				
				<tr height="25%">
					<td> </td>
					<td> </td>
					<td> </td>
					<td> </td>
				</tr>
			-->	
				<tr >
					<form action="search" method="POST">
						
						<div>
							<td width="20%" align="left">
								<strong>Location:</strong>
								<select name="location_select">
									<option value="ANY">ANY</option>
									
									<?php
										foreach($n_loc as $n)
										{
											echo '<option value="'.$n['neighbourhood'].'">'.$n['neighbourhood'].'</option>';
										}
									?>
									
								</select>
							</td>
							
						</div>
						
						<div>
							
							<td width="25%" align="center">
								<strong >Category:</strong>
								<select name="category_select">
									<option value="ANY">ANY</option>
									
									<option value="0"> Occupied Footpath </option>
									<option value="1"> Open Dustbin </option>
									<option value="2"> Open Manhole </option>
									<option value="3"> Electric Wires </option>
									<option value="4"> Waterlogging </option>
									<option value="5"> Risky Intersection </option>
									<option value="6"> No Street Light </option>
									<option value="7"> Crime Prone Area </option>
									<option value="8"> Broken Road </option>
									<option value="9"> Wrong Way Traffic </option>
								</select>
							</td>
							
						</div>
						
						<div>
							<td width="20%" align="center">
								<strong>Duration:</strong>
								<select name="duration_select">
									<option value="ANY">ANY</option>
									
									<option value="1"> Last Day </option>
									<option value="7"> Last Week </option>
									<option value="30"> Last Month </option>
									
								</select>
							</td>
							
						</div>
						
						
						<div>
							<td width="20%" align="center">
								<strong>Status:</strong>
								<select name="status_select">
									<option value="ANY">ANY</option>
									<option value="0"> Pending </option>
									<option value="1"> Verified </option>
									<option value="2"> Rejected </option>
									<option value="3"> Solved </option>
									
								</select>
							</td>
							
						</div>
						
						<div>
							<td align="left">
								<input type="submit" class="btn btn-success" style="width:80px" value = "GO">
							</td>
						</div>
					</form>
				</tr>
				
				<!--
				<tr height="25%">
					<td> </td>
					<td> </td>
					<td> </td>
					<td> </td>
					<td> </td>
				</tr>
				-->
			</tbody>
		</table>
		<hr>
		<hr>
	</div>
	
	<?php
		if($is_set)
		{
			echo '<div class="table-responsive" style="background-color:yellow;">
			<table class="table ">
				<thead>
					<tr>
						<td><font size="3"><strong>Serial No</strong></font></td>
						<td size="3"><strong>Image</strong></td>
						<td size="3"><strong>Description</strong></td>
						<td size="3"><strong>User Name(<font color="red">Rating</font>)</strong></td>
						<td size="3"><strong>Location</strong></td>
						<td size="3"><strong>Status</strong></td>
						<td size="3"><strong>...</strong></td>
					</tr>
				</thead>
				
				<tbody>';
					
					$counter=1;
					foreach($posts as $p)
					{
						echo '<tr>
					
						<form action="take_action/'.$p['post_id'].'" method="POST">
							<td>'.$counter.'</td>';
							
							echo '<td><img src="data:image/jpeg;base64,' . base64_encode($p['image']) . '" width="250px" height="200px"></td>';
			 
							if($p['category']==0) $category=" Occupied Footpath ";
							else if($p['category']==1) $category=" Open Dustbin ";
							else if($p['category']==2) $category=" Open Manhole ";
							else if($p['category']==3) $category=" Electric Wires ";
							else if($p['category']==4) $category=" Waterlogging ";
							else if($p['category']==5) $category=" Risky Intersection ";
							else if($p['category']==6) $category=" No Street Light ";
							else if($p['category']==7) $category=" Crime Prone Area ";
							else if($p['category']==8) $category=" Broken Road ";
							else if($p['category']==9) $category=" Wrong Way Traffic ";
							else $category=" Category Not Found ";
							
							echo '<td>
								<strong>Category</strong>'.$category.'
								<br><br>
								<strong>Location Got From User</strong> : '.$p['informal_location'].'
								<br><br>
								<strong>Description</strong>: '.$p['text'].' 
								<br><br>
								<strong>Votes</strong>: &nbsp;&nbsp;&nbsp;&nbsp;&uarr; <font color="blue">'.$p['up_votes'].'</font> &nbsp;&nbsp;&nbsp;&nbsp; &darr; <font color="red">'.$p['down_votes'].'</font>
								<br><br>
								<strong>Time</strong>: '.$p['time'].'
								<br>
								<a href="#"><u>view comments</u> </a></td>
							</td>
							
							<td> '.$p['user_name'].'(<font color="red">'.$p['user_rating'].'</font>) </td>
							<td> <strong>Road</strong>: '.$p['location']['route'].' <br> <strong>Neighbourhood</strong>: '.$p['location']['neighbourhood'].' <br> <strong>Locality</strong>: '.$p['location']['locality'].' <br> <br>
								<a href="#"><u>view in map</u> </a></td>
							<td> 
								<select name="action">';
								if($p['status']==0)
								{
									echo '
									<option value="0" selected>PENDING</option>
									<option value="1">VERIFIED</option>
									<option value="2">REJECTED</option>
									<option value="3">SOLVED</option>';
								}
								else if($p['status']==1)
								{
									echo '
									<option value="0">PENDING</option>
									<option value="1" selected>VERIFIED</option>
									<option value="2">REJECTED</option>
									<option value="3">SOLVED</option>';
								}
								else if($p['status']==2)
								{
									echo '
									<option value="0" selected>PENDING</option>
									<option value="1">VERIFIED</option>
									<option value="2" selected>REJECTED</option>
									<option value="3">SOLVED</option>';
								}
								else
								{
									echo'
									<option value="4" selected>SOLVED </option>
									<option value="0">PENDING</option>
									<option value="1">VERIFIED</option>
									<option value="2">REJECTED</option>';
								}
									
								echo '</select>
							</td>
							<td>
								<input type="submit" class="btn btn-info" style="width:80px" value = "UPDATE" >
							</td>
						</form>
						</tr>';
						$counter++;
					}
				echo '</tbody>
			</table>
			
		</div>';
		}
	?>
	
</div>