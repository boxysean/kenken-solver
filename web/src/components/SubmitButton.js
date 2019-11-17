import React from 'react';
import Button from 'react-pushy-buttons';
import './SubmitButton.css';
import 'react-pushy-buttons/css/pushy-buttons.css'

class SubmitButton extends React.Component {
  render() {
    return (
      <div className="ButtonWrapper">
        <Button
          size="lg"
          color="blue"
          onClick={this.props.onSubmit}
        >
          Solve!
        </Button>
      </div>
    );
  }
}

export default SubmitButton;
