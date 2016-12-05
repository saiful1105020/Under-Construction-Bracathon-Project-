 <html>
	<body>
    <h2 style="text-align:center"><b>Log Of Recent Activities</b></h2>

    <div class="container-fluid">
        <table class="table table-hover">
            <thead>
                <tr>
                    <td></td>
                    <td><b>Serial No</b></td>
                    <td><b>Message</b></td>
                    <td><b>Time</b></td>
                    <td><b>Show Post</b></td>
                </tr>
            </thead>
            <tbody>
                
                <form class="form-control" method="post" action="">
                 <?php $i=1;
                 foreach ($logData as $l) {
                        echo'<tr>
                            <td></td>
                            <td>'.$i.'</td>
                            <td>'.$l['message'].'</td>
                            <td>'.$l['time'].'</td>
                            <td>';
                                if($l['post_id']!=-1)
                                 echo'<a href="../showPost/'.$l['post_id'].'">View Post</a>
                            </td>
                        </tr>';
                        $i++;
                    }
                    ?>
                    <tr>
                        <td></td>
                        <td>
                            <?php if($index>0) echo'
                            <a href="../showLog/'.--$index.'"><b>Previous</b></a>';
                            ?>
                        </td>
                        <td></td>
                        <td></td>
                        
                        <td>
                            <?php echo'
                            <a href="../showLog/'.++$index.'"><b>Next</b></a>';
                            ?>
                        </td>
                    </tr>
                    
                </form>
                
            </tbody>
        </table>
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