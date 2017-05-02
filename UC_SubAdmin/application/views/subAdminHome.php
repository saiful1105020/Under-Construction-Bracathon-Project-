 
    <h2 style="text-align:center"><b>Search Reported Posts</b></h2>
    <div class="container-fluid">
        
        <div class="row">
            <form action="search" method="POST">
            <div class="col-md-1"></div>
            <div class="col-md-2">
                <h3 style="text-align: center"><span class="label label-danger">Location</span></h3>
                 <select class="form-control" name="location_select">
                    <option value="ANY">ANY</option>

                    <?php
                         foreach($n_loc as $n)
                        {
							
							if($search_key['location']===$n['neighbourhood'])
							{
								echo '<option value="'.$n['neighbourhood'].'" selected>'.$n['neighbourhood'].'</option>';
							}
                            else echo '<option value="'.$n['neighbourhood'].'">'.$n['neighbourhood'].'</option>';
                        }
                    ?>
                </select>
            </div>
			<div class="col-md-1"></div>
            <div class="col-md-2">
                <h3 style="text-align: center"><span class="label label-danger">Category</span></h3>
                <select class="form-control" name="category_select">
                    <option value="ANY">ANY</option>
                    <?php
						foreach($catData as $c)
						{
							if($search_key['category']===$c['categoryId'])
							{
								echo '<option value="'.$c['categoryId'].'" selected> '.$c['name'].' </option>';
							}
							else echo '<option value="'.$c['categoryId'].'"> '.$c['name'].' </option>';
						}
					?>

                </select>
            </div>
			<div class="col-md-1"></div>
            <div class="col-md-2">
                <h3 style="text-align: center"><span class="label label-danger">Duration</span></h3>
                    <select class="form-control" name="duration_select">
                        <?php
							if($search_key['duration']==="ANY")
							{
								echo '<option value="ANY" selected>ANY</option>
								<option value="1"> Last Day </option>
								<option value="7"> Last Week </option>
								<option value="30"> Last Month </option>';
							}
							else if($search_key['duration']==1)
							{
								echo '<option value="ANY">ANY</option>
								<option value="1" selected> Last Day </option>
								<option value="7"> Last Week </option>
								<option value="30"> Last Month </option>';
							}
							else if($search_key['duration']==7)
							{
								echo '<option value="ANY">ANY</option>
								<option value="1"> Last Day </option>
								<option value="7" selected> Last Week </option>
								<option value="30"> Last Month </option>';
							}
							else if($search_key['duration']==30)
							{
								echo '<option value="ANY">ANY</option>
								<option value="1"> Last Day </option>
								<option value="7"> Last Week </option>
								<option value="30" selected> Last Month </option>';
							}
						?>
                    </select>
                
            </div>
            
            <div class="col-md-1"></div>
            <div class="col-md-2">
                <h3 style="visibility: hidden">.</h3>
                <button type="submit" class="btn btn-success"><span>Go</span></button>
            </div>
        </form>
        </div>
        <br><br><br>
        <?php
        if($is_set)
        {
        echo'<div class="row">
            <table class="table table-hover">
                <thead>
                    <th>Serial</th>
                    <th>Image</th>
                    <th>Description</th>
                    <th>User Name</th>
                    <th>Location</th>
                    <th>Action</th>
                    <th>Update</th>
                </thead>
                <tbody>';
                $counter=1;
                    foreach($posts as $p)
                    {
                        //if($p['flag']==0) continue;
						
                    echo '<tr>
                        <form action="take_action/'.$p['post_id'].'" method="POST">
                            <td><b>'.$counter.'</b></td>';
                            echo '<td><img src="data:image/jpeg;base64,' . base64_encode($p['image']) . '" width="260" height="80" class="img-responsive" alt="Responsive image"></td>';
                        //<td><img src="my.png" class="img-responsive" alt="Responsive image" class="img-thumbnail" class="img-thumbnail" alt="Cinque Terre" width="260" height="180"></td>
                            $category = $p['cat_name'];

                        echo '<td>
                            <ul>
                                <li><b>Category:</b> '.$category.' </li>
                                <li><b>Location got from User:</b> '.$p['informal_location'].'</li>
                                <li><b>Description:</b> '.$p['text'].' </li>
                                <li><b>Votes:</b> &nbsp;&nbsp;&nbsp;&nbsp;&uarr; <font color="blue">'.$p['up_votes'].'</font> &nbsp;&nbsp;&nbsp;&nbsp; &darr; <font color="red">'.$p['down_votes'].'</font></li>
                                <li><b>Time: </b>'.$p['time'].'</li>
               
                            </ul>
                        </td>
                        <td>
                           <b> '.$p['user_name'].'</b>(<font color="red">'.$p['user_rating'].'</font>)
                        </td>
                        <td>
                            <ul>
                                <li><b>Road : </b> '.$p['location']['route'].'</li>
                                <li><b>NeighbourHood:</b> '.$p['location']['neighbourhood'].'</li>
                                <li><b>Locality:</b> '.$p['location']['locality'].'</li>
                                <li><a href="showALocation/'.$p['post_id'].'"><b>View in Map</b></a></li>
                            </ul>
                        </td>
                        <td>
                            <select class="form-control" name="action">';
							
							/**
								status = flag = 0: unflagged
								1: flagged
							*/
							
								if($p['flag']==0)
                                {
                                    echo '
                                    <option value="1">FLAG AS INAPPROPRIATE</option>';
                                }
                                else if($p['flag']==1)
                                {
                                    echo '
                                    <option value="0">UNFLAG</option>';
                                    
                                }
                                echo'
                        </td>
                        <td>
                            <button type="Submit" class="btn btn-primary">Update</button>
                        </td>
                        </form>
                    </tr>';
                    $counter++;
                    }

                echo'</tbody>
            </table>
        </div>
    </div>';
}?>

    

    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
    <script src="<?php echo base_url("assets/js/bootstrap.min.js"); ?>"></script>
    <!-- Just to make our placeholder images work. Don't actually copy the next line! -->
    <script src="<?php echo base_url("assets/js/vendor/holder.min.js"); ?>"></script>
    <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
    <script src="<?php echo base_url("assets/js/ie10-viewport-bug-workaround.js"); ?>"></script>
  </body>
</html>