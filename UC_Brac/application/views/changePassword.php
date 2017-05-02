<br><br><br>
<h2 style="text-align:center"><b>Change Password</b></h2>
<br>

      <form   action="<?php echo site_url('bracAdmin/changePassAction');?>" method = "post"> 
       <div class="row placeholders">
            <div class="col-md-2 placeholder"></div>
            <div class="col-md-3 placeholder">
              <h4>Old Password<h4>
            </div>
            <div class="col-md-3 placeholder">
              <input type="password" class="form-control" name="oldPassword" placeholder="Old Password" ><br>
            </div>
            <div class="col-md-4 placeholder"></div>
        </div>
        <div class="row placeholders">
            <div class="col-md-2 placeholder"></div>
            <div class="col-md-3 placeholder">
              <h4>New Password<h4>
            </div>
            <div class="col-md-3 placeholder">
              <input type="password" class="form-control" name="newPassword" placeholder="New Password" ><br>
            </div>
            <div class="col-md-4 placeholder"></div>
        </div>
        <div class="row placeholders">
            <div class="col-md-2 placeholder"></div>
            <div class="col-md-3 placeholder">
              <h4>Confirm New Password<h4>
            </div>
            <div class="col-md-3 placeholder">
              <input type="password" class="form-control" name="confirmPassword" placeholder="Confirm New Password" ><br>
            </div>
            <div class="col-md-4 placeholder"></div>
        </div>
        <div class="row placeholders">
          <div class="col-md-4 placeholder"></div>
          <div class="col-md-4 placeholder">
            <button type="submit" class="btn btn-success">Confirm</button>
          </div>
        </div>
      </form>

    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
    <script src="js/bootstrap.min.js"></script>
    <!-- Just to make our placeholder images work. Don't actually copy the next line! -->
    <script src="js/vendor/holder.min.js"></script>
    <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
    <script src="js/ie10-viewport-bug-workaround.js"></script>
  </body>
</html>
