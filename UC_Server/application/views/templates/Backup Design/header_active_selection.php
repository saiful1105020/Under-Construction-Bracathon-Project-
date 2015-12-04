<!DOCTYPE html>
<html lang="en"> 
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Under Construction - Admin</title>
    
	<link rel="stylesheet" href="<?php echo base_url("assets/css/bootstrap.min.css"); ?>" />
	<link rel="stylesheet" href="<?php echo base_url("assets/css/bootstrap-theme.min.css"); ?>" />
	<link rel="stylesheet" href="<?php echo base_url("assets/css/admin_home.css"); ?>" />
    
	    
	<script type="text/javascript" src="<?php echo base_url("assets/js/jquery-1.11.2.min.js"); ?>"></script>
	<script type="text/javascript" src="<?php echo base_url("assets/js/bootstrap.js"); ?>"></script>

  </head> 

  <body style="background-color: #778899">

    <nav class="navbar navbar-inverse">
      <div class="container-fluid">
        <!-- Brand and toggle get grouped for better mobile display -->
        <div class="navbar-header">
          <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="#" style="color: 0xfff;"> Under Construction</a>
        </div>

        <!-- Collect the nav links, forms, and other content for toggling -->
        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
		<?php
			if($current_nav === "home")
			{
				echo '
				<ul class="nav navbar-nav">
		  
					<li class="active"><a href="'.site_url("admin").'" class="glyphicon glyphicon-home"> HOME <span class="sr-only">(current)</span></a></li>
					
				</ul>
				  
				<ul class="nav navbar-nav navbar-right" id="">
					<li><a href="'.site_url('admin/logout').'" class="glyphicon glyphicon-log-out"> Sign-Out </a></li>
				</ul>';
			}
			//test
			else if($current_nav === "logout")
			{
				echo '
				<ul class="nav navbar-nav">
		  
					<li><a href="'.site_url('admin').'" class="glyphicon glyphicon-home"> HOME <span class="sr-only">(current)</span></a></li>
					
				</ul>
				  
				<ul class="nav navbar-nav navbar-right" id="">
					<li class="active"><a href="'.site_url('admin/logout').'" class="glyphicon glyphicon-log-out"> Sign-Out </a></li>
				</ul>';
			}
			//end of test
		?>
		  
        </div><!-- /.navbar-collapse -->
      </div><!-- /.container-fluid -->
    </nav>