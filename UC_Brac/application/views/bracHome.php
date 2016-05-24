 <html>
	<body>
    <h2 style="text-align:center"><b>Search Reported Posts</b></h2>

    <div class="container-fluid">
        <table class="table table-hover">
            <thead>
                <tr>
                    <td>Serial No</td>
                    <td>Log data</td>
                </tr>
            </thead>
            <tbody>
                
                <form class="form-control" method="post" action="">
                    <?php 
                    foreach($logData as $l){
                        echo "<tr>
                            <td></td>
                            <td>".$l['string']."</td>
                        </tr>
                    }";
                        echo'<a href=""><b>Next</b></a>
                        <a href=""><b>Previous</b></a>
                    ;'
                    ?>
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