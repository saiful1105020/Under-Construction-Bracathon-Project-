 
    <h2 style="text-align:center"><b>Delete Category</b></h2>

    <div class="container-fluid">
        <div class="col-md-3"></div>
        <div class="col-md-6" style="overflow-y: auto; height:450px;">
            <table class="table table-hover">
                <thead >
                    <tr>
                        <td><b><h3>Existing Category</h3></b></td>

                    </tr>
                </thead>
                <tbody>
					<?php foreach($existingCat as $e){
						echo'
						<tr>
							<form method="POST" action="deleteCategoryAction/'.$e['categoryId'].'">
								<td>'.$e['name'].'</td>
								<td><button type="submit" class="btn btn-danger btn-md">Delete</button></td>	
							</form>
						</tr>';
						}
					?>
                </tbody>
            </table>
        </div>
        <div class="col-md-3"></div>
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