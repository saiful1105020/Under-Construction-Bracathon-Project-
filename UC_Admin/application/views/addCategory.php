 
    <h2 style="text-align:center"><b>Add New Category</b></h2>

    <div class="container-fluid" >
        <div class="col-md-1"></div>
        <div class="col-md-5" style="overflow-y: auto; height:400px;">
            <table class="table table-hover">
                <thead >
                    <tr>
                        <td><b><h3>Suggested Category</h3></b></td>
                        <td><b><h3>Count</h3></b></td>
                        <td><b><h3>Add To List</h3></b></td>
                    </tr>
                </thead>
                <tbody>
				<?php foreach($catData as $c){
                    echo'<tr>
					<form action="addCategoryAction/'.$c['id'].'" method="POST">
                        <td>'.$c['name'].'</td>
                        <td>'.$c['count'].'</td>
                        <td> <button type="submit" class="btn btn-primary btn-md">Approve</button></td>
					</form>
                    </tr>';
					}
					?>
                </tbody>
            </table>
        </div>
        <div class="col-md-2"></div>
        <div class="col-md-3" style="overflow-y: auto; height:400px;">
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
                        <td>'.$e['name'].'</td>
                    </tr>';
					}
				?>
                </tbody>
            </table>
        </div>
        <div class="col-md-1"></div>
    </div>
    <br>

    <h2>
        <div class="container-fluid">
			<form action="addNewCategory" method="POST">
				<div class="col-md-2"></div>
				<div class="col-md-3"><span class="label label-default">Category Name: </span></div>
				<div class="col-md-4"><input type="text" class="form-control" placeholder="enter new category name" name="newCat"></div>
				<div class="col-md-3"><button type="submit" class="btn btn-success btn-md">Submit</button></div>
			</form>
        </div>
    </h2>
    

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