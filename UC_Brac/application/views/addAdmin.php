 <br><br><br>
    <h2 style="text-align:center"><b>Create/Remove Admin</b></h2>

    <div class="container-fluid" >
        <div class="col-md-1"></div>
        <div class="col-md-5" style="overflow-y: auto; height:430px;">
          
          <h3 style="text-align:center"><font color="green">Create Admin</font></h3>
          <br><br>
            <form   action="<?php echo site_url('bracAdmin/addAdminAction');?>" method = "post"> 
             <div class="row placeholders">
                  
                  <div class="col-md-4 placeholder">
                    <h4>Name<h4>
                  </div>
                  <div class="col-md-6 placeholder">
                    <input type="text" class="form-control" name="name" placeholder="Admin Name" ><br>
                  </div>
                  <div class="col-md-2 placeholder"></div>
              </div>
              <div class="row placeholders">
                  
                  <div class="col-md-4 placeholder">
                    <h4>Password<h4>
                  </div>
                  <div class="col-md-6 placeholder">
                    <input type="password" class="form-control" name="password" placeholder="Password" ><br>
                  </div>
                  <div class="col-md-2 placeholder"></div>
              </div>
      
              <div class="row placeholders">
                <div class="col-md-4 placeholder"></div>
                <div class="col-md-4 placeholder">
                  <button type="submit" class="btn btn-success">Create</button>
                </div>
              </div>
            </form>
        </div>
        <div class="col-md-2"></div>
        <div class="col-md-3" style="overflow-y: auto; height:430px;">
          <h3 style="text-align:center"><font color="red">Delete Admin</font></h3>
          
            <table class="table table-hover">
                <thead >
                    <tr>
                        <td><b><h4>Admin List</h4></b></td>
                        <td></td>
                    </tr>
                </thead>
                <tbody>
                    
					<?php foreach($adminList as $a){
					echo'<tr>
						<form method="POST" action="deleteAdminAction/'.$a['admin_id'].'">
						  <td>'.$a['admin_name'].'</td>
						  <td><button type="submit" class="btn btn-danger btn-md">Delete</button></td>
						</form>
					</tr>';
						}
					?>
                    
                    
                </tbody>
            </table>
        </div>
        <div class="col-md-1"></div>
    </div>
    <br>

    

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