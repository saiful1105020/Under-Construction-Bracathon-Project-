	<br><br><br>
    <h2 style="text-align:center"><b>Selected Post</b></h2>

    <div class="container-fluid">
        
        
        <div class="row">
            <table class="table table-hover">
                <thead>
                    <th>Serial</th>
                    <th>Image</th>
                    <th>Description</th>
                    <th>User Name</th>
                    <th>Location</th>
                    <th>Status</th>
                    
                </thead>
                <tbody>
                    <tr>
                        <td><b>1</b></td>
                        <?php echo'
                        <td><img src="data:image/jpeg;base64,' . base64_encode($post['image']) . '" width="260" height="80" class="img-responsive" alt="Responsive image"></td>
                        
                        <td>
                            <ul>
                                <li><b>Category:</b>'.$post['cat_name'].' </li>
                                <li><b>Location got from User:</b>'.$post['informal_location'].'</li>
                                <li><b>Description:</b> '.$post['text'].'</li>
                                <li><b>Votes:</b> &nbsp;&nbsp;&nbsp;&nbsp;&uarr; <font color="blue">'.$post['up_votes'].'</font> &nbsp;&nbsp;&nbsp;&nbsp; &darr; <font color="red">'.$post['down_votes'].'</font></li>
                                <li><b>Time: </b>'.$post['time'].'</li>
                            </ul>
                        </td>
                        <td>
                           <b> '.$post['user_name'].'</b>(<font color="red">'.$post['user_rating'].'</font>)
                        </td>
                        <td>
                            <ul>
                                <li><b>Road : </b> '.$post['location']['route'].'</li>
                                <li><b>NeighbourHood:</b> '.$post['location']['neighbourhood'].'</li>
                                <li><b>Locality:</b> '.$post['location']['locality'].'</li>
                                <!--<li><a href="showALocation/'.$post['post_id'].'"><b>View in Map</b></a></li>-->
                            </ul>
                        </td>
                        <td>
                            <select class="form-control" name="action">';
                            if($post['status']==0)
                                {
                                    echo '
                                    <option value="0" selected>PENDING</option>';
                                }
                                else if($post['status']==1)
                                {
                                    echo '
                                    
                                    <option value="1" selected>VERIFIED</option>';
                                }
                                else if($post['status']==2)
                                {
                                    echo '
                                    
                                    <option value="2" selected>REJECTED</option>';
                                }
                                else
                                {
                                    echo'
                                    <option value="4" selected>SOLVED </option>';
                                }
                                ?>    
                                </select>
                        </td>
                        </form>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>


    

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